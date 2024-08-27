package zerodot.usercenter.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import zerodot.usercenter.mapper.UserMapper;
import zerodot.usercenter.modol.domain.User;
import zerodot.usercenter.service.UserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@SpringBootTest
/**
 * 用户测试
 * @author
 */
class InsertSQLUserServiceImplTest {
    /**
     * 自定义线程池 默认的线程池和计算机的物理核心数目和逻辑核心数目有关
     * 线程的初始数目 线程的最大数目 线程的存活时间 线程的能保存的任务的数量
     * CPU密集型主要是加减乘除算数运算：分配的核心线程数 = CPU - 1
     * IO 密集型号 主要是网络访问或者数据传输 消息队列等 ：分配的核心数目可以大于CPU数目
     */
    private ExecutorService executorService = new ThreadPoolExecutor(20,100,10000, TimeUnit.MINUTES,new LinkedBlockingDeque<>(10000));
    /**
     * 批量插入数据
     */
    @Resource
    private UserMapper userMapper;
    @Test
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


    @Resource
    private UserService userService;
    @Test
    public void doInsetUserByBatch(){
        final int INSERT_NUM = 10000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ArrayList<User> userArrayList = new ArrayList<>();
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
//            添加user 用户
            userArrayList.add(user);
//            插入数据
//            userMapper.insert(user);

        }
        userService.saveBatch(userArrayList,100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


    @Test
    public void doInsetUserByParrel(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ArrayList<User> userArrayList = new ArrayList<>();
            for (int k = 0; k < 10000; k++) {
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
        //            添加user 用户
                userArrayList.add(user);
            }
            System.out.println("userArrayList size: " + userArrayList.size());
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                //异步执行 使用默认的线程池
                System.out.println("ThreadName:" + Thread.currentThread().getName());
                System.out.println("userArrayList size: " + userArrayList.size());
                userService.saveBatch(userArrayList, 10000);
            },executorService); //调用自定义线程池
            futureList.add(future);  //相当于拿到了10个异步任务
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        //join 阻塞 直到执行完所有的任务 才会执行接下来的代码
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}