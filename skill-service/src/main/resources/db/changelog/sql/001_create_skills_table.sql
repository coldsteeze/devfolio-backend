CREATE TABLE skills
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(50) UNIQUE NOT NULL,
    category   VARCHAR(50)        NOT NULL,
    active     BOOLEAN            NOT NULL,
    created_at TIMESTAMP          NOT NULL,
    updated_at TIMESTAMP
)