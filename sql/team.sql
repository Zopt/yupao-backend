-- auto-generated definition
create table team
(
    id           bigint auto_increment comment '用户id'    primary key,
    name         varchar(256)                             null comment '队伍名称',
    description  varchar(1024)                            null comment '描述',
    maxNum       int      default 1                       null comment '最大人数',
    expireTime   datetime                                 null comment '过期时间',
    password     varchar(512)                             null comment '密码',
    userId       bigint                                        comment '用户id',
    status       int      default 0                       not null comment '0-公开 1-私有 2-加密',
    createTime   datetime default CURRENT_TIMESTAMP       null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP       null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                       not null comment '是否删除'
)
    comment '队伍';

create table user_team
(
    id           bigint auto_increment comment '队伍id'    primary key,
    userId       bigint                                        comment '用户id',
    teamId       bigint                                        comment '队伍id',
    joinTime     datetime                                 null comment '加入时间',
    createTime   datetime default CURRENT_TIMESTAMP       null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP       null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                       not null comment '是否删除'

)
    comment '用户队伍关系';