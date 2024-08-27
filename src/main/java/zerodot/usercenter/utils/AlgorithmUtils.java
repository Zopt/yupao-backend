package zerodot.usercenter.utils;


import java.util.List;
import java.util.Objects;

/**
 * 算法工具类
 */
public class AlgorithmUtils {

    /**
     * 编辑举例算法（用户计算相似度）
     * @param tagList1
     * @param tagList2
     * @return
     */
    public static int editDistance (List<String> tagList1, List<String> tagList2) {
        // write code here
        int n1 = tagList1.size();
        int n2 = tagList2.size();
        //dp[i][j]表示到str1[i]和str2[j]为止的子串需要的编辑距离
        int[][] dp = new int[n1 + 1][n2 + 1];
        //初始化边界
        for (int i = 1; i <= n1; i++)
            dp[i][0] = dp[i - 1][0] + 1;
        for (int i = 1; i <= n2; i++)
            dp[0][i] = dp[0][i - 1] + 1;
        //遍历第一个字符串的每个位置
        for (int i = 1; i <= n1; i++)
            //对应第二个字符串每个位置
            for (int j = 1; j <= n2; j++) {
                //若是字符相同，此处不用编辑
                if (Objects.equals(tagList1.get(i - 1), tagList2.get(j - 1)))
                    //直接等于二者前一个的距离
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    //选取最小的距离加上此处编辑距离1
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
            }
        return dp[n1][n2];
    }

    /**
     * 编辑举例算法（用户计算相似度）
     * @param str1
     * @param str2
     * @return
     */
    public static int editDistance (String str1, String str2) {
        // write code here
        int n1 = str1.length();
        int n2 = str2.length();
        //dp[i][j]表示到str1[i]和str2[j]为止的子串需要的编辑距离
        int[][] dp = new int[n1 + 1][n2 + 1];
        //初始化边界
        for (int i = 1; i <= n1; i++)
            dp[i][0] = dp[i - 1][0] + 1;
        for (int i = 1; i <= n2; i++)
            dp[0][i] = dp[0][i - 1] + 1;
        //遍历第一个字符串的每个位置
        for (int i = 1; i <= n1; i++)
            //对应第二个字符串每个位置
            for (int j = 1; j <= n2; j++) {
                //若是字符相同，此处不用编辑
                if (str1.charAt(i - 1) == str2.charAt(j - 1))
                    //直接等于二者前一个的距离
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    //选取最小的距离加上此处编辑距离1
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
            }
        return dp[n1][n2];
    }
}
