package zerodot.usercenter.service.impl;


import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
     void test(){
        List<String> list = new ArrayList<>();
        list.add("Redisson");
//        list.get(0);
//        list.remove(0);
        System.out.println("list: "+list.get(0));


        // Redisson
        RList<String> rList = redissonClient.getList("list-test");
        rList.add("Redisson");
        System.out.println("rlist: " + rList.get(0));
//        rList.remove(0);

    }
}
