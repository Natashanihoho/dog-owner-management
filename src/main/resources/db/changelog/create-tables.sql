--liquibase formatted sql

--changeset nhardziyevich:create-owner-table

CREATE TABLE IF NOT EXISTS owner
(
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR(64) NOT NULL,
    last_name  VARCHAR(64) NOT NULL,
    age        INT NOT NULL CHECK (age > 0),
    city       VARCHAR(64) NOT NULL
    );

--rollback DROP TABLE owner

--changeset nhardziyevich:create-dog-table

CREATE TABLE IF NOT EXISTS dog
(
    id                      SERIAL PRIMARY KEY,
    breed                   VARCHAR(128) UNIQUE NOT NULL,
    average_life_expectancy INT NOT NULL CHECK (average_life_expectancy > 0),
    origin_country          VARCHAR(64) NOT NULL,
    easy_to_train           BOOLEAN NOT NULL
);

--rollback DROP TABLE dog

--changeset nhardziyevich:create-dog-owner-table

CREATE TABLE dog_owner
(
    owner_id INT REFERENCES owner (id) ON DELETE CASCADE,
    dog_id   INT REFERENCES dog (id) ON DELETE CASCADE,
    PRIMARY KEY (owner_id, dog_id)
);

--rollback DROP TABLE dog_owner
