package zerodot.usercenter.service.impl;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import zerodot.usercenter.modol.domain.Team;
import zerodot.usercenter.modol.domain.User;
import zerodot.usercenter.modol.domain.UserTeam;

@SpringBootTest
public class TeamDemo {


    @Test
    public void test() {
        Team team = new Team();
        System.out.println(team.toString());
        User user = new User();
        System.out.println(user.toString());

        UserTeam userTeam = new UserTeam();
        System.out.println(userTeam.toString());
    }
}
