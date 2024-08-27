package zerodot.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zerodot.usercenter.modol.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-06-24 17:34:14
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param plantCode 星球编号
     * @return 返回用户id
     */

    long userRegister(String userAccount, String userPassword, String checkPassword,String plantCode);

    /**
     * 用户登录
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @return 返回脱敏之后用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUserByTag(List<String> tagNameList);


    /**
     * 更新用户信息
     * @param user
     * @return
     */

    int updateUser(User user,User loginUser);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 是否为管理员
     *
     * @param request
     * @return boolen
     */
     boolean isAdmin(HttpServletRequest request);


    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return boolen
     */
     boolean isAdmin(User loginUser);

    /**
     * 匹配用户
     *
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
