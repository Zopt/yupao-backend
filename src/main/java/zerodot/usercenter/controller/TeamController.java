package zerodot.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import zerodot.usercenter.common.BaseResponse;
import zerodot.usercenter.common.DeleteRequest;
import zerodot.usercenter.common.ErrorCode;
import zerodot.usercenter.common.ResultUtils;
import zerodot.usercenter.expection.BusinessException;
import zerodot.usercenter.modol.DTO.TeamQuery;
import zerodot.usercenter.modol.domain.Team;
import zerodot.usercenter.modol.domain.User;
import zerodot.usercenter.modol.domain.UserTeam;
import zerodot.usercenter.modol.request.TeamAddRequest;
import zerodot.usercenter.modol.request.TeamJoinRequest;
import zerodot.usercenter.modol.request.TeamQuitRequest;
import zerodot.usercenter.modol.request.TeamUpdateRequest;
import zerodot.usercenter.modol.vo.TeamUserVO;
import zerodot.usercenter.service.TeamService;
import zerodot.usercenter.service.UserService;
import zerodot.usercenter.service.UserTeamService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 队伍接口
 *
 * @author zerodot
 */
@Slf4j
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(deleteRequest.getId(), loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除失败");
        }

        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新失败");
        }

        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }

        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> list = teamService.listTeams(teamQuery, isAdmin);
        final List<Long> userTeamIdList = list.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> userTeamListQueryWapper = new QueryWrapper<>();
        try{
            User loginUser = userService.getLoginUser(request);
            userTeamListQueryWapper.eq("userId",loginUser.getId());
            userTeamListQueryWapper.in("teamId", userTeamIdList);
            List<UserTeam> userList = userTeamService.list(userTeamListQueryWapper);
            Set<Long> HasJoinuserTeamIdSet = userList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            list.forEach(team ->{
                boolean hasJoin = HasJoinuserTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        }catch (Exception e){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserTeam> userTeamJoinListQueryWapper = new QueryWrapper<>();
        userTeamJoinListQueryWapper.in("teamId",userTeamIdList);
        List<UserTeam> userTeamsList = userTeamService.list(userTeamJoinListQueryWapper);
        //每个加入队伍的用户列表
        Map<Long, List<UserTeam>> teamIdTeamUserList = userTeamsList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        list.forEach(team ->{
            team.setHasJoinNum(teamIdTeamUserList.getOrDefault(team.getId(),new ArrayList<>()).size());
        });
        return ResultUtils.success(list);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWapper);

        return ResultUtils.success(resultPage);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);

        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);

        return ResultUtils.success(result);
    }

    /**
     * 我创建的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> list = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(list);
    }


    /**
     * 我加入的队伍
     *
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamsList = userTeamService.list(userTeamQueryWrapper);
        Map<Long, List<UserTeam>> listMap = userTeamsList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVO> list = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(list);
    }

    @PostMapping("/match")
    public BaseResponse<List<User>> mathUsers(long num, HttpServletRequest request){
        if (num <= 0 || num > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, loginUser));
    }

}
