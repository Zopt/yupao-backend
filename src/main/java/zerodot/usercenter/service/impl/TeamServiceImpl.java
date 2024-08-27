package zerodot.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.View;
import zerodot.usercenter.common.ErrorCode;
import zerodot.usercenter.expection.BusinessException;
import zerodot.usercenter.mapper.TeamMapper;
import zerodot.usercenter.modol.DTO.TeamQuery;
import zerodot.usercenter.modol.Enum.TeamStatusEnum;
import zerodot.usercenter.modol.domain.Team;
import zerodot.usercenter.modol.domain.User;
import zerodot.usercenter.modol.domain.UserTeam;
import zerodot.usercenter.modol.request.TeamJoinRequest;
import zerodot.usercenter.modol.request.TeamQuitRequest;
import zerodot.usercenter.modol.request.TeamUpdateRequest;
import zerodot.usercenter.modol.vo.TeamUserVO;
import zerodot.usercenter.modol.vo.UserVO;
import zerodot.usercenter.service.TeamService;
import zerodot.usercenter.service.UserService;
import zerodot.usercenter.service.UserTeamService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-08-07 18:49:42
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    UserTeamService userTeamService;

    @Resource
    UserService userService;
    @Autowired
    private View error;

    @Resource
    RedissonClient redissonClient;

    @Override
    public long addTeam(Team team, User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不满足要求");
        }
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态错误");
        }
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(enumByValue) && (StringUtils.isBlank(password) || password.length() > 512)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
        }

        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
        // 校验用户最多创建5个队伍
        final long userId = loginUser.getId();

        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//        log.error(queryWrapper.toString());
        queryWrapper.eq("userId", userId);
        long hasTeam = this.count(queryWrapper);
        if (hasTeam >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        // 插入队伍信息到队伍表中
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍添加失败");
        }

        //插入用户到 =》 用户队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = this.userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍添加失败");
        }
        return 0;

    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWapper = new QueryWrapper<>();
        //组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name) && name.length() > 512) {
                queryWapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description) && description.length() > 512) {
                queryWapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWapper.ge("maxNum", maxNum);//大于等于
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWapper.eq("userId", userId);
            }
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NOT_AUTH);
            }
            queryWapper.eq("status", statusEnum.getValues());
            if (status != null && status > 0) {// status >= 0
                queryWapper.eq("status", status);
            }
        }

        //不展示过期队伍
        queryWapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return Collections.emptyList();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        //关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            //用户脱敏
            UserVO userVO = new UserVO();
            if (user != null) {
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }

        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        if (oldTeam.getId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍数据不存在");
        }
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (enumByValue.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.NOT_AUTH, "加密房间必须要有密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);

        return this.updateById(updateTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取队伍信息
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getTeamById(teamId);

        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum enumByValue = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(enumByValue)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(enumByValue)) {
            if (password.isEmpty() || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR, "密码为空或者错误");
            }
        }
        Long userId = loginUser.getId();
        //分布式锁，只有一个线程可以获取锁
        RLock lock = redissonClient.getLock("yupao:Join_Team");
        try {
            while (true){
                if (lock.tryLock(0,30000, TimeUnit.MILLISECONDS)) {
                    System.out.println("加锁线程Id"+Thread.currentThread().getName());
                    //查询用户已经加入的队伍数量
                    QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("userId", userId);
                    //过期的队伍筛选掉
                    long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
                    if (hasJoinNum > 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多加入或创建5个队伍");
                    }

                    userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("userId", userId);
                    userTeamQueryWrapper.eq("teamId", teamId);
                    long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
                    if (hasUserJoinTeam > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经加入队伍");
                    }
                    //查询队伍中已经加入的人数
                    long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
                    if (teamHasJoinNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR, "队伍已满");
                    }
                    //修改队伍信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());

                    return userTeamService.save(userTeam);
                }
            }
        } catch (InterruptedException e) {
            log.error("redissonClientError" + e);
            return false;
        }finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                System.out.println("释放锁线程Id"+Thread.currentThread().getName());
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        Long userId = loginUser.getId();
        UserTeam queryUserTeam = new UserTeam();
        queryUserTeam.setTeamId(teamId);
        queryUserTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入该队伍");
        }
        long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
        if (teamHasJoinNum == 1) {
            //在用户队伍关系表中删除和该队伍相关的数据，并在队伍表中删除该队伍。
            this.removeById(teamId);
        } else {
            //是队长
            if (team.getUserId().equals(userId)) {
                //把队伍转移给最早加入的用户
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamsList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamsList) || userTeamsList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUser = userTeamsList.get(1);
                Long nextUserId = nextUser.getUserId();
                //更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextUserId);
                boolean update = this.updateById(updateTeam);
                if (!update) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新队长失败");
                }
            }

        }
        return userTeamService.remove(queryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, User loginUser) {
        //校验用户是否存在
        Team team = getTeamById(id);
        Long teamId = team.getId();
        //校验是不是队长
        if (!team.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_AUTH, "无访问权限");
        }
        //移除所有的加入队伍的关联关系
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean remove = userTeamService.remove(queryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除队伍关联关系失败");
        }
        //删除队伍
        return this.removeById(teamId);
    }

    /**
     * 根据teamId查询相关的用户Id;查询加入队伍的人数
     *
     * @param teamId
     * @return
     */
    public long countTeamUserByTeamId(long teamId) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        return userTeamService.count(queryWrapper);
    }

    /**
     * 根据Id获取队伍信息
     *
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        return team;
    }
}




