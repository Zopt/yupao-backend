package zerodot.usercenter.modol.DTO;


import lombok.Data;
import lombok.EqualsAndHashCode;
import zerodot.usercenter.common.PageRequest;

import java.util.List;

/**
 * 查询封装类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户id列表
     */
    private List<Long> idList;

    /**
     * 搜索关键词(同时对描述名称和描述搜索)
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0-公开 1-私有 2-加密
     */
    private Integer status;

}
