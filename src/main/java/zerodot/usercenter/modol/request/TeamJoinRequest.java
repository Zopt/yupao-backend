package zerodot.usercenter.modol.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamJoinRequest implements Serializable {
    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;




    private static final long serialVersionUID = 5370734827738556087L;
}
