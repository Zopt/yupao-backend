package zerodot.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import zerodot.usercenter.modol.DTO.TeamQuery;
import zerodot.usercenter.modol.domain.Team;
import zerodot.usercenter.modol.domain.User;
import zerodot.usercenter.modol.request.TeamJoinRequest;
import zerodot.usercenter.modol.request.TeamQuitRequest;
import zerodot.usercenter.modol.request.TeamUpdateRequest;
import zerodot.usercenter.modol.vo.TeamUserVO;

import java.util.List;

/**
* @author Administrator
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-08-07 18:49:42
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */


    public long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    /**
     * 推出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除（解散）队伍
     *
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id,User loginUser);
}
