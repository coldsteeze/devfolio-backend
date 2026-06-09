CREATE TABLE career_entries
(
    id           UUID PRIMARY KEY,
    user_id      UUID         NOT NULL,
    type         VARCHAR(20)  NOT NULL,
    title        VARCHAR(150) NOT NULL,
    organization VARCHAR(150) NOT NULL,
    description  VARCHAR(2000),
    start_month  INTEGER      NOT NULL,
    start_year   INTEGER      NOT NULL,
    end_month    INTEGER,
    end_year     INTEGER,

    CONSTRAINT fk_career_entries_user_profiles
        FOREIGN KEY (user_id)
        REFERENCES user_profiles(user_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_career_entries_user_id
    ON career_entries (user_id);