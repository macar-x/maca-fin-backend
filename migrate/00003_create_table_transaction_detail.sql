-- ----------------------------
-- Database Migration File No.003
-- 创建交易详情数据表
-- @author Emmett
-- @since 2025/07/27
-- ----------------------------

CREATE SCHEMA IF NOT EXISTS backend;
-- ----------------------------
-- Table structure for transaction_detail.
-- Fields not include: Account, Merchant, Location, Payment Method, Reference Number, Tax&Tip;
-- ----------------------------
-- DROP TABLE IF EXISTS backend.transaction_detail;
CREATE TABLE IF NOT EXISTS backend.transaction_detail
(
    id              BIGINT                              NOT NULL PRIMARY KEY,
    category_id     BIGINT                              NULL,
    user_id         BIGINT                              NOT NULL,
    belongs_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    amount          BIGINT                              NOT NULL,
    -- todo(emmett): Could be config as a ENV value for default when deploy.
    currency        VARCHAR(8)                          NOT NULL,
    description     VARCHAR(512)                        NULL,
    created_by      VARCHAR(64)                         NOT NULL,
    created_user_id BIGINT                              NOT NULL,
    created_at      TIMESTAMP                           NOT NULL,
    updated_by      VARCHAR(64)                         NOT NULL,
    updated_user_id BIGINT                              NOT NULL,
    updated_at      TIMESTAMP                           NOT NULL,
    deleted_by      VARCHAR(64)                         NULL,
    deleted_user_id BIGINT                              NULL,
    deleted_at      TIMESTAMP                           NULL,
    deleted         BOOLEAN   DEFAULT FALSE             NOT NULL
);

CREATE INDEX transaction_detail_user_id_index
    ON backend.transaction_detail (user_id);

ALTER TABLE backend.transaction_detail
    OWNER TO maca;

COMMENT ON COLUMN backend.transaction_detail.user_id IS 'belongs to specifics user; should not be null;';
COMMENT ON COLUMN backend.transaction_detail.amount IS 'unit 0.01;';
COMMENT ON COLUMN backend.transaction_detail.currency IS 'refer to ISO 4217';
