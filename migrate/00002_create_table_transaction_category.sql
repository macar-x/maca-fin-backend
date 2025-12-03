-- ----------------------------
-- Database Migration File No.002
-- 创建交易类别数据表
-- @author Emmett
-- @since 2025/07/27
-- ----------------------------

CREATE SCHEMA IF NOT EXISTS backend;
-- ----------------------------
-- Table structure for transaction_category.
-- ----------------------------
-- DROP TABLE IF EXISTS backend.transaction_category;
CREATE TABLE IF NOT EXISTS backend.transaction_category
(
    id              BIGINT                NOT NULL PRIMARY KEY,
    parent_id       BIGINT                NULL,
    user_id         BIGINT                NULL,
    name            VARCHAR(64)           NOT NULL,
    description     VARCHAR(512)          NULL,
    logo_path       VARCHAR(256)          NOT NULL,
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

CREATE INDEX transaction_category_user_id_index
    ON backend.transaction_category (user_id);

ALTER TABLE backend.transaction_category
    OWNER TO maca;

COMMENT ON COLUMN backend.transaction_category.parent_id IS 'belongs to another category as parent;';
COMMENT ON COLUMN backend.transaction_category.user_id IS 'belongs to specifics user; null for all users;';

-- Insert initial data only if the table is empty (newly created)
-- This prevents duplicate data if the script is run multiple times
INSERT INTO backend.transaction_category (id, parent_id, user_id, name, description, logo_path, created_by,
                                          created_user_id, created_at, updated_by, updated_user_id, updated_at,
                                          deleted_by, deleted_user_id, deleted_at, deleted)
SELECT *
FROM (SELECT 0               as id,
             NULL::BIGINT    as parent_id,
             NULL::BIGINT    as user_id,
             '未分类'        as name,
             '系统默认分类'  as description,
             'default.ico'   as logo_path,
             'system'        as created_by,
             -1              as created_user_id,
             now()           as created_at,
             'system'        as updated_by,
             -1              as updated_user_id,
             now()           as updated_at,
             NULL::VARCHAR   as updated_by,
             NULL::BIGINT    as updated_user_id,
             NULL::TIMESTAMP as updated_at,
             FALSE           as deleted) AS init_data
WHERE NOT EXISTS (SELECT 1 FROM backend.transaction_category);
