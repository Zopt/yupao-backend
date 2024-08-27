package zerodot.usercenter.service.impl;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import zerodot.usercenter.utils.AlgorithmUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 算法工具类测试
 */

@SpringBootTest
public class AlgorithmUtilsTest {


   @Test
    public void AlgorithmUtils(){


       String str1 = "田野黄希";
       String str3 = "田野华西";
       String str2 = "黄西";
       int score1 = AlgorithmUtils.editDistance(str1, str3);
       int score2 = AlgorithmUtils.editDistance(str1, str2);
       System.out.println(score1);
       System.out.println(score2);
   }

   @Test
   public void test(){
      List<String> tagList1 = Arrays.asList("Java", "大一", "男");
      List<String> tagList2 = Arrays.asList("Java", "大一", "女");
      List<String> tagList3 = Arrays.asList("Python", "大二", "女");
      int score1 = AlgorithmUtils.editDistance(tagList1, tagList2);
      int score2 = AlgorithmUtils.editDistance(tagList1, tagList3);
      System.out.println(score1);
      System.out.println(score2);
   }
}
