ALTER TABLE users
    ADD COLUMN keycloak_id VARCHAR(255) NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT uq_users_keycloak_id UNIQUE (keycloak_id);

ALTER TABLE users
    ADD CONSTRAINT uq_users_email UNIQUE (email);