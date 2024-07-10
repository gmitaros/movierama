INSERT IGNORE INTO users (first_name, last_name, email, password, account_locked, enabled, created_date)
VALUES ('Test', 'Integration', 'integration3@test.com', '$2a$10$lG1O3ZA0EYOlpMZpUGCazOKjmjnohf9RGtPlaPgrcIzJnAdlPfPSm',
        0, 1, NOW());

INSERT IGNORE INTO users (first_name, last_name, email, password, account_locked, enabled, created_date)
VALUES ('Test', 'Integration', 'integration2@test.com', '$2a$10$lG1O3ZA0EYOlpMZpUGCazOKjmjnohf9RGtPlaPgrcIzJnAdlPfPSm',
        0, 1, NOW());

INSERT INTO users_roles (roles_id, user_id)
VALUES ((select id from roles where name = 'USER'), (select id from users where email = 'integration3@test.com'));
INSERT INTO users_roles (roles_id, user_id)
VALUES ((select id from roles where name = 'USER'), (select id from users where email = 'integration2@test.com'));

INSERT IGNORE INTO movies (id, title, description, publication_date, user_id, created_date, last_modified_date,
                           created_by, last_modified_by)
VALUES (14, 'Pulp Fiction',
        'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.',
        '1994-10-14', (select id from users where email = 'integration2@test.com'), '2024-04-21 10:57:56', null, 1,
        null);

INSERT IGNORE INTO movies (id, title, description, publication_date, user_id, created_date, last_modified_date,
                           created_by, last_modified_by)
VALUES (15, 'Pulp Fiction22222222222',
        'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.',
        '1994-10-14', (select id from users where email = 'integration3@test.com'), '2024-04-21 10:57:56', null, 1,
        null);

INSERT IGNORE INTO movies (id, title, description, publication_date, user_id, created_date, last_modified_date,
                           created_by, last_modified_by)
VALUES (16, 'Test',
        'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.',
        '1994-10-14', (select id from users where email = 'movierama@movierama.com'), '2024-04-21 10:57:56', null,
        (select id from users where email = 'movierama@movierama.com'),
        null);

INSERT IGNORE INTO votes (id, movie_id, type, user_id, created_date, last_modified_date, created_by,
                          last_modified_by)
VALUES (1, 16, 'HATE', (select id from users where email = 'integration3@test.com'), '2024-04-22 23:07:30', null, 4,
        null);

