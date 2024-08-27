package zerodot.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zerodot.usercenter.modol.domain.User;
import zerodot.usercenter.service.UserService;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
/**
 * 用户测试
 * @author
 */
class UserServiceImplTest {

    @Resource
    private UserService userService;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void testAddUser(){
        User user = new User();

        user.setUserName("zerodot");
        user.setUserAccount("zerodot");
        user.setAvatarUrl("http://www.cqinfo.top/wp-content/uploads/2024/06/zerodot_%E5%89%AF%E6%9C%AC-300x110.png");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("18888888888");
        user.setEmail("12222@qq.com");


        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);


    }

    @Test
    void userRegister() {
        String userAccount = "zerodot";
        String userPassword = "";
        String checkPassword = "123456";
        String plantCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, plantCode);
        Assertions.assertEquals(-1,result);

        userPassword = "1234";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,plantCode);
        Assertions.assertEquals(-1,result);

        userAccount = "zero dot";
        result = userService.userRegister(userAccount, userPassword, checkPassword,plantCode);
        Assertions.assertEquals(-1,result);

        userAccount = "zer";
        result = userService.userRegister(userAccount, userPassword, checkPassword,plantCode);
        Assertions.assertEquals(-1,result);
    }

    @Test
    void testSearchUserByTags(){
        List<String> tagNameList = Arrays.asList("Java");
        List<User> userList = userService.searchUserByTag(tagNameList);
        userList.forEach(System.out::println);
//        Assertions.assertNotNull(userList);
    }

    @Test
    void testSearchUserByName(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("userName","yuqi10");
        List<User> userList = userService.list(queryWrapper);
        List<User> collect = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        collect.forEach(System.out::println);
    }


}