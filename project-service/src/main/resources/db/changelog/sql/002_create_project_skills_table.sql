CREATE TABLE project_skills
(
    id             UUID PRIMARY KEY,
    project_id     UUID    NOT NULL,
    skill_id       UUID    NOT NULL,
    manually_added BOOLEAN NOT NULL,
    confirmed      BOOLEAN NOT NULL,
    CONSTRAINT fk_project_skills_project FOREIGN KEY (project_id) REFERENCES projects (id)
)