package zerodot.usercenter.modol.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author zerodot
 */
@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = -6095758422415627557L;

    private String userAccount;

    private String userPassword;

}
