CREATE TABLE project_favorites
(
    id         UUID PRIMARY KEY,
    user_id    UUID      NOT NULL,
    project_id UUID      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_project UNIQUE (user_id, project_id)
);

CREATE INDEX idx_fav_user_id ON project_favorites (user_id);