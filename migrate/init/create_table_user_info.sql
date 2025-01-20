DROP TABLE IF EXISTS BACKEND.USER_INFO;
CREATE TABLE BACKEND.USER_INFO
(
    id           BIGINT PRIMARY KEY          NOT NULL,
    role_id      BIGINT                      NOT NULL,
    username     VARCHAR(64) UNIQUE          NOT NULL,
    password     VARCHAR(256)                NOT NULL,
    mobile_phone VARCHAR(32)                 NOT NULL,
    email        VARCHAR(128)                NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at   TIMESTAMP WITHOUT TIME ZONE NULL     DEFAULT NULL,
    deleted      BOOLEAN                     NOT NULL DEFAULT FALSE
);

ALTER TABLE BACKEND.USER_INFO
    OWNER TO MACA;
-- CREATE INDEX user_info_idx_username ON BACKEND.USER_INFO (username);

INSERT INTO BACKEND.USER_INFO(id, role_id, username, password, mobile_phone, email)
VALUES (5840767775932416, 1, 'root', 'Eg5cDP0Sdg1p6SMS2MatEw==$Ao8fEBwqlkLivPjuGuxf+KSDOXUkvkgzCaX5NEkLsmY=',
        '13813813888', 'root@macacloud.com'),
       (5840767775932417, 2, 'admin', 'pm3q8fQltEtyrSntan73Yw==$Ijc4cUe4+NHX8lqJwXQZzLvPgu8TyVGKJsXijJmb49E=',
        '13813813888', 'admin@macacloud.com'),
       (5840767775932418, 3, 'user', '8WbkZ9Pu3bS7Em1Uzzrv4Q==$UG6PsgJvIKBJJQe4bdkHFdnP5D0rHK1z6WUuKGtAt8A=',
        '13813813888', 'user@macacloud.com'),
       (5840767775932419, 4, 'guest', 'kRrc4XjgpNB64Rn+zf1+PQ==$eEATYxNY9ayoSYbXWod0u5XIw6RuBBM9kk5ruaqzGM8=',
        '13813813888', 'guest@macacloud.com');
