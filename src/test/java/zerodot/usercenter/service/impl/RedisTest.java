package zerodot.usercenter.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import zerodot.usercenter.modol.domain.User;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;


    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增加数据
        valueOperations.set("RedisInt", 1);
        valueOperations.set("RedisString", "Redis");
        valueOperations.set("RedisDouble", 1042.22);
        //修改数据
        valueOperations.set("RedisDouble", 12.111);
        //删除数据
        redisTemplate.delete("RedisInt");
        User user = new User();
        user.setUserName("Redis同学");
        user.setUserAccount("RedisStudent");
        user.setAvatarUrl("http://images.cqinfo.top/i/2024/07/06/668947b6ac504.jpg");
        user.setGender(0);
        user.setUserPassword("143a3b7804ffda0ac65b4df0f0ae1cf0");
        user.setPhone("15451554455");
        user.setEmail("154515@redis.com");
        user.setUserStatus(0);
        user.setUserRole(0);
        user.setPlantCode("77777");
        user.setTags("[\"Spring Data Redis\",\"Redis\",\"男\"]");
        user.setProfile("成为Redis开发工程师");

        valueOperations.set("RedisUser", user);
        //查询数据
        Assertions.assertEquals(1, valueOperations.get("RedisInt"));
        Assertions.assertEquals("Redis", valueOperations.get("RedisString"));
        Assertions.assertEquals(1042.22, valueOperations.get("RedisDouble"));

        System.out.println(valueOperations.get("RedisUser"));


    }

}
