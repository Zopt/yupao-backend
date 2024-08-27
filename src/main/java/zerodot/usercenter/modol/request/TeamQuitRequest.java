package zerodot.usercenter.modol.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户推出队伍请求体·
 */
@Data
public class TeamQuitRequest implements Serializable {
    /**
     * 队伍id
     */
    private Long teamId;






    private static final long serialVersionUID = 5370734827738556087L;
}
