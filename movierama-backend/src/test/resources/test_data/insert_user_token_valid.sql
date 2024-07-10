INSERT INTO users (first_name, last_name, email, password, account_locked, enabled, created_date)
VALUES ('Test', 'Integration', 'integration3@test.com', '$2a$10$lG1O3ZA0EYOlpMZpUGCazOKjmjnohf9RGtPlaPgrcIzJnAdlPfPSm',
        0, 0, NOW());

INSERT INTO users_roles (roles_id, user_id)
VALUES ((select id from roles where name = 'USER'), (select id from users where email = 'integration3@test.com'));

INSERT INTO token (token, created_at, expires_at, validated_at, user_id)
VALUES ('2424234', '2024-04-19 23:45:36', DATEADD('MINUTE', 10, CURRENT_TIMESTAMP), null, (select id from users where email = 'integration3@test.com'));
