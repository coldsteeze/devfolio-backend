CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    email      VARCHAR(254) NOT NULL UNIQUE,
    password   VARCHAR(60)  NOT NULL,
    logged_at  TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);