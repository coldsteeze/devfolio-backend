CREATE TABLE portfolios
(
    user_id        UUID PRIMARY KEY,
    nickname       VARCHAR   NOT NULL UNIQUE,
    first_name     VARCHAR   NOT NULL,
    last_name      VARCHAR   NOT NULL,
    bio            TEXT,
    total_projects SMALLINT  NOT NULL,
    updated_at     TIMESTAMP NOT NULL
)