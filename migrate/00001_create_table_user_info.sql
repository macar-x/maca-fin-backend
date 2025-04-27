------------------------------
-- Table Creation: User Info
-- @authors Emmett
-- @since 2025/04/26
------------------------------

-- DROP TABLE IF EXISTS BACKEND.USER_INFO;
CREATE TABLE IF NOT EXISTS BACKEND.USER_INFO
(
    id           BIGINT PRIMARY KEY          NOT NULL,
    username     VARCHAR(64) UNIQUE          NOT NULL,
    password     VARCHAR(256)                NOT NULL,
    roles        VARCHAR(256)                NOT NULL,
    mobile_phone VARCHAR(32)                 NOT NULL,
    email        VARCHAR(128)                NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      BOOLEAN                     NOT NULL DEFAULT FALSE
);

ALTER TABLE BACKEND.USER_INFO
    OWNER TO MACA;
CREATE INDEX IF NOT EXISTS user_info_idx_username ON BACKEND.USER_INFO (username);
