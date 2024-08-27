package zerodot.usercenter.modol.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户的包装类（脱敏）
 * @TableName user
 */
@Data
public class UserVO implements Serializable {
    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 登录账户
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户角色 普通用户-0 管理员-1
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String plantCode;

    /**
     * 标签
     */
    private String tags;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}