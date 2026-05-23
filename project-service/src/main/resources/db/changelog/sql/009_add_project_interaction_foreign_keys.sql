ALTER TABLE project_likes
    ADD CONSTRAINT fk_project_likes_project
        FOREIGN KEY (project_id)
            REFERENCES projects (id)
            ON DELETE CASCADE;

ALTER TABLE project_views
    ADD CONSTRAINT fk_project_views_project
        FOREIGN KEY (project_id)
            REFERENCES projects (id)
            ON DELETE CASCADE;