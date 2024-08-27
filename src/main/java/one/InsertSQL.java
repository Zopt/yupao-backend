package one;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import zerodot.usercenter.mapper.UserMapper;
import zerodot.usercenter.modol.domain.User;

import javax.annotation.Resource;

@Component
public class InsertSQL {
    @Resource
    private UserMapper userMapper;


    /**
     * 批量插入数据
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsetUser(){
        final int INSERT_NUM = 10000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();

            user.setUserName("贾同学");
            user.setUserAccount("fakeStudent");
            user.setAvatarUrl("http://images.cqinfo.top/i/2024/07/06/668947b6ac504.jpg");
            user.setGender(0);
            user.setUserPassword("143a3b7804ffda0ac65b4df0f0ae1cf0");
            user.setPhone("15451554455");
            user.setEmail("154515@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlantCode("77777");
            user.setTags("[\"Java\",\"全栈工程师\",\"男\"]");
            user.setProfile("成为全栈开发工程师");
//            插入数据
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
