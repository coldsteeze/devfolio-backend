CREATE TABLE verified_skills
(
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    project_skill_id UUID NOT NULL,
    skill_id UUID NOT NULL,
    skill_name VARCHAR NOT NULL,
    verified_at TIMESTAMPTZ NOT NULL
)