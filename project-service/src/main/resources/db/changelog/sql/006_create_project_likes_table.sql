CREATE TABLE project_likes
(
    id         UUID PRIMARY KEY,
    project_id UUID      NOT NULL,
    user_id    UUID      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uq_like_project_user UNIQUE (project_id, user_id)
);

CREATE INDEX idx_likes_project_id ON project_likes (project_id);