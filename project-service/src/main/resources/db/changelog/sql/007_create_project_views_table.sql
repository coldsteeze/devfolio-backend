CREATE TABLE project_views
(
    id         UUID PRIMARY KEY,
    project_id UUID      NOT NULL,
    user_id    UUID      NOT NULL,
    viewed_at  TIMESTAMP NOT NULL
);

CREATE INDEX idx_views_project_id ON project_views (project_id);