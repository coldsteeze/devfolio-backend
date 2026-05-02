CREATE TABLE project_images
(
    id         UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    image_url  TEXT
)