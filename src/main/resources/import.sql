-- This file allow to write SQL commands that will be emitted in test and dev.
INSERT INTO fruit(id, name) VALUES (1, 'Cherry');
INSERT INTO fruit(id, name) VALUES (2, 'Apple');
INSERT INTO fruit(id, name) VALUES (3, 'Banana');
ALTER SEQUENCE fruit_seq RESTART WITH 4;

INSERT INTO user_info(id, username, password, role, email, mobilePhone, deleted, deleted_at)
VALUES (1, 'root', 'root', 'root', 'root@example.com', '00000000', false, null);
INSERT INTO user_info(id, username, password, role, email, mobilePhone, deleted, deleted_at)
VALUES (2, 'admin', 'admin', 'admin', 'admin@example.com', '00000000', false, null);
INSERT INTO user_info(id, username, password, role, email, mobilePhone, deleted, deleted_at)
VALUES (3, 'user', 'user', 'user', 'user@example.com', '00000000', false, null);
INSERT INTO user_info(id, username, password, role, email, mobilePhone, deleted, deleted_at)
VALUES (4, 'guest', 'guest', 'guest', 'guest@example.com', '00000000', false, null);
ALTER SEQUENCE user_info_seq RESTART WITH 5;
