--liquibase formatted sql

--changeset nhardziyevich:add-email

ALTER TABLE owner
    ADD email VARCHAR(128) UNIQUE NOT NULL;

--rollback ALTER TABLE owner DROP COLUMN email

--changeset nhardziyevich:rename-breed-column

ALTER TABLE breed
    RENAME COLUMN breed TO breed_name;

--rollback ALTER TABLE breed RENAME COLUMN breed_name TO breed;

--changeset nhardziyevich:create-breed-table

CREATE TABLE IF NOT EXISTS dog
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(64) NOT NULL,
    date_of_birth DATE,
    breed_id      INT REFERENCES breed(id),
    owner_id      INT REFERENCES owner(id)
);

--rollback DROP TABLE breed

--changeset nhardziyevich:delete-breed-owner-table

DROP TABLE dog_owner;

--rollback CREATE TABLE dog_owner
--rollback (
--rollback owner_id INT REFERENCES owner (id) ON DELETE CASCADE,
--rollback dog_id   INT REFERENCES breed (id) ON DELETE CASCADE,
--rollback PRIMARY KEY (owner_id, dog_id)
--rollback );

