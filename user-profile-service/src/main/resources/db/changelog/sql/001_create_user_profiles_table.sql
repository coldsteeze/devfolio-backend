CREATE TABLE user_profiles
(
    user_id    UUID PRIMARY KEY,
    nickname   VARCHAR(50) UNIQUE,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),
    bio        TEXT,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);