package zerodot.usercenter.constant;

/**
 *
 * 用户常量
 * @author zerodot
 */
public interface UserConstant {
    /**
     * 用户登录态键
     */
     String USER_LOGIN_STATUS = "user_login_status";

    /**
     * 管理员用户-1
     * 普通用户-0
     */
    Integer DEFAULT_ROLE = 0;
    Integer ADMIN_ROLE = 1;
}
