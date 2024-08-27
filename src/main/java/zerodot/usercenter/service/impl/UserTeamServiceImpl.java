package zerodot.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import zerodot.usercenter.mapper.UserTeamMapper;
import zerodot.usercenter.modol.domain.UserTeam;
import zerodot.usercenter.service.UserTeamService;

/**
* @author Administrator
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-08-07 18:51:26
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




