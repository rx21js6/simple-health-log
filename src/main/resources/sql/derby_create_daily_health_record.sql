CREATE TABLE daily_health_record
(
    login_user_id INT NOT NULL REFERENCES login_user(id),
    posted_date date NOT NULL,

    temp_morning DOUBLE,
    temp_evening DOUBLE,
    condition_note VARCHAR(32672),

    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,


    PRIMARY KEY (login_user_id, posted_date)
)
