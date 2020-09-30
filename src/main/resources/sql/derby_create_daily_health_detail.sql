-- DROP TABLE daily_health_detail


CREATE TABLE daily_health_detail
(
    login_user_id INT,
    posted_date DATE,
    template_id INT,

    health_detail_id INT NOT NULL,
    value INT,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    PRIMARY KEY (login_user_id, posted_date, template_id),
    FOREIGN KEY (login_user_id, posted_date) REFERENCES daily_health_record(login_user_id, posted_date),
    FOREIGN KEY (template_id) REFERENCES daily_health_detail_template(id)
)

