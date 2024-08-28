package zerodot.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import zerodot.usercenter.common.ErrorCode;
import zerodot.usercenter.constant.UserConstant;
import zerodot.usercenter.expection.BusinessException;
import zerodot.usercenter.mapper.UserMapper;
import zerodot.usercenter.modol.domain.User;
import zerodot.usercenter.service.UserService;
import zerodot.usercenter.utils.AlgorithmUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static zerodot.usercenter.constant.UserConstant.USER_LOGIN_STATUS;

/**
 * @author Administrator
 * @author zerodot
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-06-24 17:34:14
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;
    // 盐值 混淆密码
    private static final String SALT = "zerodot";

    @Resource
    RedissonClient redissonClient;


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String plantCode) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, userPassword, plantCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (plantCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        //校验账户不包含特殊字符
        String validPattern = ".*[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*()——+|{}【】‘；：”“’。，、？\\\\]+.*";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }
        //密码和校验密码相同
        if (!checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "二次输入密码不相同");
        }
        RLock lock = redissonClient.getLock("yupao:Join_Team");
        try {
            while (true){
                //账户不能重复
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("userAccount", userAccount);
                long count = userMapper.selectCount(queryWrapper);
                if (count < 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
                }
                //星球编号
                queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("plantCode", plantCode);
                count = userMapper.selectCount(queryWrapper);
                if (count > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号已注册");
                }
                //2.加密
                String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
                //3.插入数据
                User user = new User();
                user.setUserAccount(userAccount);
                user.setUserPassword(encryptPassword);
                user.setPlantCode(plantCode);
                boolean saveResult = this.save(user);
                if (!saveResult) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "信息保存失败");
                }
                return user.getId();
            }
        } finally {
            //只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                System.out.println("释放锁线程Id"+Thread.currentThread().getName());
                lock.unlock();
            }
        }
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR, "账户或密码为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度小于4");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8");
        }

        //校验账户不包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }

        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login fail,userAccount:{} can't match userPassword", userAccount);
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR, "用户不存在,或密码错误");
        }
        //3. 用户脱敏
        User safeUser = getSafetyUser(user);


        //4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATUS, safeUser);


        return safeUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUserName(originUser.getUserName());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setGender(originUser.getGender());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserRole(originUser.getUserRole());
        safeUser.setPlantCode(originUser.getPlantCode());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setTags(originUser.getTags());
        safeUser.setProfile(originUser.getProfile());
        return safeUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */

    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return 1;
    }

    /**
     * 根据标签查询用户cache方法 内存过滤
     *
     * @param tagNameList
     * @return
     */

    @Override
    public List<User> searchUserByTag(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1.先查询所有用户
        QueryWrapper queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //2.在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            Set<String> tempTagNameList = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameList = Optional.ofNullable(tempTagNameList).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameList.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        return (User) userObj;
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        //仅管理员可见
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATUS);
        User user = (User) userObj;
        return user != null && user.getUserRole().equals(UserConstant.ADMIN_ROLE);
    }

    @Override
    public boolean isAdmin(User loginUser) {
        //仅管理员可见
        return loginUser != null && Objects.equals(loginUser.getUserRole(), UserConstant.ADMIN_ROLE);
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());

        List<Pair<User, Long>> list = new ArrayList<>();
        //依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            //无标签
            if (StringUtils.isBlank(userTags) || Objects.equals(user.getId(), loginUser.getId())) {
                continue;
            }
            //有标签
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());

            long distance = AlgorithmUtils.editDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        //用户列表和相关的相似度
        List<Pair<User, Long>> topUserPairList = list.stream()
                        .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num).collect(Collectors.toList());
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",userIdList);
        Map<Long, List<User>> userIdListMap =  this.list(queryWrapper)
                .stream().map(this::getSafetyUser).collect(Collectors.groupingBy(User::getId));
        ArrayList<User> resultUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            resultUserList.add(userIdListMap.get(userId).get(0));
        }
        return resultUserList;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        Long userId = user.getId();
        //如果是管理员允许更新任意用户
        //如果不是管理员，只允许更新当前自己的
        if (!isAdmin(loginUser) && !Objects.equals(userId, loginUser.getId())) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    /**
     * 根据标签查询用户 sql方法
     *
     * @param tagNameList
     * @return
     */

    @Deprecated
    private List<User> searchUserByTagBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接 and 查询
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }
}




