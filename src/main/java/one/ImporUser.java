package one;

import com.alibaba.excel.EasyExcel;

import java.util.List;


public class ImporUser {

    public static void main(String[] args) {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
        String fileName =  "src/main/java/one/testExcel.xlsx";

//        readByListener(fileName);
        synchronousRead(fileName);

    }

    /**
     * 监听器读
     * @param fileName
     */
    public static void readByListener(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取3000条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, UserDate.class, new UserDataListener()).sheet().doRead();

    }


    /**
     * 同步读
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<UserDate> userDateList = EasyExcel.read(fileName).head(UserDate.class).sheet().doReadSync();
        System.out.println(userDateList.size());
    }
}
