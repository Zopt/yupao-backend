-- auto-generated definition
create table user
(
    userName     varchar(256) collate utf8mb4_0900_ai_ci  null comment '用户昵称',
    id           bigint auto_increment comment '用户id'
        primary key,
    userAccount  varchar(256)                             null comment '登录账户',
    avatarUrl    varchar(1024)                            null comment '头像',
    gender       tinyint                                  null comment '性别',
    userPassword varchar(512)                             not null comment '密码',
    phone        varchar(128)                             null comment '电话',
    email        varchar(512)                             null comment '邮箱',
    userStatus   int      default 0                       null comment '用户状态',
    createTime   datetime default CURRENT_TIMESTAMP       null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP       null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                       not null comment '是否删除',
    userRole     int      default 0                       not null comment '用户角色 0-普通用户 1-管理员用户',
    plantCode    varchar(512)                             null comment '星球编号',
    tags         varchar(1024) collate utf8mb4_0900_ai_ci null comment '标签 Json',
    profile      varchar(512) collate utf8mb4_0900_ai_ci  null comment '个人简介'
)
    comment '用户' row_format = DYNAMIC;

