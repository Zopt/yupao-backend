package one;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserDate {



    @ExcelProperty("成员编号")
    private String plantCode;

    @ExcelProperty("成员昵称")
    private String userName;

    private Double doubleData;
}