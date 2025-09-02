CREATE TABLE user_profiles
(
    user_id    UUID PRIMARY KEY,
    nickname   VARCHAR(50) NOT NULL UNIQUE,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP
);