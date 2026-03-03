CREATE TABLE projects
(
    id             UUID PRIMARY KEY,
    user_id        UUID         NOT NULL,
    name           VARCHAR(100) NOT NULL,
    description    VARCHAR(1000),
    github_url     VARCHAR(500) NOT NULL,
    project_public BOOLEAN      NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP
)