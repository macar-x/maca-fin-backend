-- ----------------------------
-- Database Migration File No.001
-- 创建系统用户数据表
-- @author Emmett
-- @since 2025/05/22
-- ----------------------------

CREATE SCHEMA IF NOT EXISTS backend;
-- ----------------------------
-- Table structure for user_info.
-- ----------------------------
-- DROP TABLE IF EXISTS backend.user_info;
CREATE TABLE IF NOT EXISTS backend.user_info
(
    id              BIGINT                NOT NULL PRIMARY KEY,
    username        VARCHAR(64)           NOT NULL UNIQUE,
    PASSWORD        VARCHAR(256)          NOT NULL,
    roles           VARCHAR(256)          NOT NULL,
    nickname        VARCHAR(256)          NULL,
    avatar_url      VARCHAR(256)          NULL,
    mobile_phone    VARCHAR(32)           NOT NULL,
    email           VARCHAR(128)          NOT NULL,
    created_by      VARCHAR(64)           NOT NULL,
    created_user_id BIGINT                NOT NULL,
    created_at      TIMESTAMP             NOT NULL,
    updated_by      VARCHAR(64)           NOT NULL,
    updated_user_id BIGINT                NOT NULL,
    updated_at      TIMESTAMP             NOT NULL,
    deleted_by      VARCHAR(64)           NULL,
    deleted_user_id BIGINT                NULL,
    deleted_at      TIMESTAMP             NULL,
    deleted         BOOLEAN DEFAULT FALSE NOT NULL
);

ALTER TABLE backend.user_info OWNER TO maca;
