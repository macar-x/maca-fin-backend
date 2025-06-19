------------------------------
-- Table Creation: Record Category
-- @authors Emmett
-- @since 2025/05/02
------------------------------

-- DROP TABLE IF EXISTS BACKEND.RECORD_CATEGORY;
CREATE TABLE IF NOT EXISTS BACKEND.RECORD_CATEGORY
(
    id         BIGINT PRIMARY KEY          NOT NULL,
    parent_id  BIGINT                      NULL,
    user_id    BIGINT                      NULL,
    name       VARCHAR(128)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted    BOOLEAN                     NOT NULL DEFAULT FALSE
);

ALTER TABLE BACKEND.RECORD_CATEGORY
    OWNER TO MACA;
CREATE INDEX IF NOT EXISTS user_id_idx_username ON BACKEND.RECORD_CATEGORY (user_id);

-- insert default data for record category if not exists
INSERT INTO BACKEND.RECORD_CATEGORY (id, parent_id, user_id, name, created_at, updated_at, deleted)
VALUES
    (1, NULL, 1, 'Default Category', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE)