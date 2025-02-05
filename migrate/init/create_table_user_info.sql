DROP TABLE IF EXISTS BACKEND.USER_INFO;
CREATE TABLE BACKEND.USER_INFO
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
-- CREATE INDEX user_info_idx_username ON BACKEND.USER_INFO (username);

INSERT INTO BACKEND.USER_INFO(id, username, password, roles, mobile_phone, email)
VALUES (5840767775932416, 'admin', 'Eg5cDP0Sdg1p6SMS2MatEw==$Ao8fEBwqlkLivPjuGuxf+KSDOXUkvkgzCaX5NEkLsmY=',
        'admin,user', '13813813888', 'admin@macacloud.com');
