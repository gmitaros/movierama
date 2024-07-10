INSERT INTO roles (name, created_date) VALUES ('USER', NOW());

INSERT INTO users (first_name, last_name, email, password, account_locked, enabled, created_date)
VALUES ('movie', 'rama', 'movierama@movierama.com', '$2a$10$lG1O3ZA0EYOlpMZpUGCazOKjmjnohf9RGtPlaPgrcIzJnAdlPfPSm', 0, 1, NOW());

INSERT INTO users_roles (roles_id, user_id)
VALUES ((select id from roles where name = 'USER'), (select id from users where email = 'movierama@movierama.com'))