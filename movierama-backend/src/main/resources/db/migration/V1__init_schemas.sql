CREATE TABLE roles
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(255) UNIQUE NOT NULL,
    created_date       DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME            NULL ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);

-- Users table
CREATE TABLE users
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    first_name         VARCHAR(255)        NOT NULL,
    last_name          VARCHAR(255)        NOT NULL,
    email              VARCHAR(255) UNIQUE NOT NULL,
    password           VARCHAR(255)        NOT NULL,
    account_locked     BOOLEAN             NOT NULL,
    enabled            BOOLEAN             NOT NULL,
    created_date       DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME            NULL ON UPDATE CURRENT_TIMESTAMP
);

-- User roles relation table
CREATE TABLE users_roles
(
    roles_id INT NOT NULL,
    user_id  INT NOT NULL
);


ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (roles_id) REFERENCES roles (id);
ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

-- Token table
create table token
(
    id           int auto_increment
        primary key,
    token        varchar(255) not null,
    created_at   datetime     null,
    expires_at   datetime     null,
    validated_at datetime     null,
    user_id      int          not null,
    constraint token
        unique (token),
    constraint token_ibfk_1
        foreign key (user_id) references users (id)
);

create index userId
    on token (user_id);

-- Movie table
CREATE TABLE movies
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    title              VARCHAR(255) NOT NULL,
    description        TEXT         NOT NULL,
    publication_date   DATE         NOT NULL,
    user_id            INT REFERENCES users (id),
    created_date       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by         INT          NOT NULL,
    last_modified_by   INT
);

-- Vote table
CREATE TABLE votes
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    movie_id           BIGINT                NOT NULL,
    type               VARCHAR(255)          NOT NULL,
    user_id            INT                   NOT NULL,
    created_date       datetime              NOT NULL,
    last_modified_date datetime              NULL,
    created_by         INT                   NOT NULL,
    last_modified_by   INT                   NULL,
    CONSTRAINT pk_votes PRIMARY KEY (id)
);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_MOVIE FOREIGN KEY (movie_id) REFERENCES movies (id);
ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);