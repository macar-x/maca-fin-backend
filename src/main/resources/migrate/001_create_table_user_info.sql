-- Database Migration File No.001
-- 创建系统用户数据表
-- @author Emmett
-- @since 2025/05/22

-- ----------------------------
-- Table structure for user_info.
-- ----------------------------
-- DROP TABLE IF EXISTS backend.user_info;
CREATE TABLE IF NOT EXISTS backend.user_info
(
    id           BIGINT                              NOT NULL PRIMARY KEY,
    username     VARCHAR(64)                         NOT NULL UNIQUE,
    password     VARCHAR(256)                        NOT NULL,
    roles        VARCHAR(256)                        NOT NULL,
    nickname     VARCHAR(256)                        NULL,
    avatar_url   VARCHAR(256)                        NULL,
    mobile_phone VARCHAR(32)                         NOT NULL,
    email        VARCHAR(128)                        NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,
    created_by   BIGINT    DEFAULT -1                NULL,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL,
    updated_by   BIGINT    DEFAULT -1                NULL,
    deleted_at   TIMESTAMP                           NULL,
    deleted_by   BIGINT                              NULL,
    deleted      BOOLEAN   DEFAULT FALSE             NOT NULL
);

ALTER TABLE backend.user_info
    OWNER TO maca;

-- Insert initial data only if the table is empty (newly created)
-- This prevents duplicate data if the script is run multiple times
INSERT INTO backend.user_info (id, username, password, roles, nickname, avatar_url, mobile_phone, email)
SELECT *
FROM (SELECT 5840767775932416,
             'admin',
             'Eg5cDP0Sdg1p6SMS2MatEw==$Ao8fEBwqlkLivPjuGuxf+KSDOXUkvkgzCaX5NEkLsmY=',
             'admin,user',
             'Administrator',
             '/data/avatar/default.jpg',
             '13813813888',
             'admin@macacloud.com') AS init_data
WHERE NOT EXISTS (SELECT 1 FROM backend.user_info);
