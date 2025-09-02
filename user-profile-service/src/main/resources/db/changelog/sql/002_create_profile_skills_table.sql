CREATE TABLE profile_skills
(
    user_id UUID         NOT NULL,
    skill   VARCHAR(100) NOT NULL,
    CONSTRAINT fk_profile_skills_user
        FOREIGN KEY (user_id)
            REFERENCES user_profiles (user_id)
            ON DELETE CASCADE,
    PRIMARY KEY (user_id, skill)
);