CREATE TABLE profile_links
(
    user_id   UUID         NOT NULL,
    link_type VARCHAR(20)  NOT NULL,
    url       VARCHAR(500) NOT NULL,
    CONSTRAINT fk_profile_links_user
        FOREIGN KEY (user_id)
            REFERENCES user_profiles (user_id)
            ON DELETE CASCADE,
    PRIMARY KEY (user_id, link_type)
);