INSERT IGNORE INTO users (first_name, last_name, email, password, account_locked, enabled, created_date)
VALUES ('Test', 'Integration', 'integration5@test.com', '$2a$10$lG1O3ZA0EYOlpMZpUGCazOKjmjnohf9RGtPlaPgrcIzJnAdlPfPSm',
        0, 0, NOW());

INSERT IGNORE INTO users (first_name, last_name, email, password, account_locked, enabled, created_date)
VALUES ('Test', 'Integration', 'integration6@test.com', '$2a$10$lG1O3ZA0EYOlpMZpUGCazOKjmjnohf9RGtPlaPgrcIzJnAdlPfPSm',
        0, 0, NOW());

INSERT IGNORE INTO users_roles (roles_id, user_id)
VALUES ((select id from roles where name = 'USER'), (select id from users where email = 'integration5@test.com'));

INSERT IGNORE INTO token (token, created_at, expires_at, validated_at, user_id)
VALUES ('123456', '2024-04-19 23:45:36', '2024-04-20 00:00:36', null, (select id from users where email = 'integration5@test.com'));

INSERT IGNORE INTO token (token, created_at, expires_at, validated_at, user_id)
VALUES ('123444', '2024-04-19 23:45:36', DATEADD('MINUTE', 10, CURRENT_TIMESTAMP), DATEADD('MINUTE', 11, CURRENT_TIMESTAMP), (select id from users where email = 'integration6@test.com'));