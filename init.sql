# 创建身份表
create table t_role
(
    id      bigint auto_increment primary key,
    name    varchar(255) not null unique,
    name_zh varchar(255) not null
);
# 创建用户表
create table t_user
(
    id       bigint auto_increment primary key,
    password varchar(255) not null,
    username varchar(255) not null unique ,
    enabled  bool         not null
);
# 创建用户身份对应表
create table t_users_roles
(
    id      bigint auto_increment primary key,
    user_id bigint not null,
    role_id bigint not null,
    constraint FK7l00c7jb4804xlpmk1k26texy
        foreign key (user_id) references t_user (id),
    constraint FKj47yp3hhtsoajht9793tbdrp4
        foreign key (role_id) references t_role (id)
);
# 持久化令牌
create table persistent_logins
(
    username  varchar(64) not null,
    series    varchar(64) primary key,
    token     varchar(64) not null,
    last_used timestamp   not null
);
# 生成角色数据
INSERT INTO t_role (name, name_zh)
VALUES ('ROLE_admin', '管理员'),
       ('ROLE_user', '普通用户');