CREATE TABLE portfolio_projects
(
    project_id     UUID PRIMARY KEY,
    portfolio_id   UUID         NOT NULL REFERENCES portfolios (user_id) ON DELETE CASCADE,
    name           VARCHAR(100) NOT NULL,
    description    VARCHAR(1000),
    github_url     VARCHAR(500) NOT NULL,
    project_public BOOLEAN      NOT NULL
)