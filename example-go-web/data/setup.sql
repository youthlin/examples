CREATE TABLE IF NOT EXISTS user
(
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name         VARCHAR(255) NOT NULL DEFAULT '' COMMENT '用户名',
    display_name VARCHAR(255) NOT NULL DEFAULT '' COMMENT '显示名',
    email        VARCHAR(255) NOT NULL DEFAULT '' COMMENT '邮件地址',
    password     CHAR(32)     NOT NULL DEFAULT '' COMMENT '密码',
    create_at    DATETIME     NOT NULL DEFAULT now() COMMENT '创建时间',
    unique uniq_name (name),
    unique uniq_email (email),
    key idx_name_password (name, password)
) ENGINE INNODB
  CHARSET utf8mb4 COMMENT '用户表';


CREATE TABLE IF NOT EXISTS thread
(
    id        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    topic     VARCHAR(255) NOT NULL DEFAULT '' COMMENT '话题',
    content   text         NOT NULL COMMENT '内容',
    tags      varchar(255) NOT NULL DEFAULT '' COMMENT '内容',
    user_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '发起人',
    create_at DATETIME     NOT NULL DEFAULT now() COMMENT '创建时间',
    key idx_topic (topic),
    key idx_user_id (user_id)
) ENGINE INNODB
  CHARSET utf8mb4 COMMENT '帖子列表';

CREATE TABLE IF NOT EXISTS post
(
    id        BIGINT   NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    content   text     NOT NULL COMMENT '内容',
    user_id   BIGINT   NOT NULL DEFAULT 0 COMMENT '发起人',
    thread_id BIGINT   NOT NULL DEFAULT 0 COMMENT '话题 ID',
    pid       BIGINT   NOT NULL DEFAULT 0 COMMENT '回复的跟帖ID',
    create_at DATETIME NOT NULL DEFAULT now() COMMENT '创建时间',
    key idx_user_id (user_id),
    key idx_thread_id (thread_id)
) ENGINE INNODB
  CHARSET utf8mb4 COMMENT '跟帖列表';
