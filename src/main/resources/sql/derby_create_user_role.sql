CREATE TABLE user_role
(
    id integer NOT NULL,
    role_name VARCHAR(20),
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    CONSTRAINT user_role_pkey PRIMARY KEY (id),
    CONSTRAINT user_role_id_fkey FOREIGN KEY (id) REFERENCES login_user(id)
)
