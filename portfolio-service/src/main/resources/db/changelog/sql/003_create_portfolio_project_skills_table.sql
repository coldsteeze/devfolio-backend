CREATE TABLE portfolio_project_skills
(
    portfolio_project_id     UUID    NOT NULL REFERENCES portfolio_projects (project_id) ON DELETE CASCADE,
    skill_name     VARCHAR NOT NULL,
    skill_category VARCHAR NOT NULL,
    confirmed      BOOLEAN NOT NULL,

    PRIMARY KEY (portfolio_project_id, skill_name)
);