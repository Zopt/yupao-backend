package zerodot.usercenter.common;


import lombok.Data;

import java.io.Serializable;

/**
 *  通用删除请求类
 */

@Data
public class DeleteRequest implements Serializable {


    private static final long serialVersionUID = 4028630358504007649L;

    private long id;
}
