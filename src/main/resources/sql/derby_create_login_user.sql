CREATE TABLE login_user
(
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY,
    login_id VARCHAR(12) NOT NULL,
    name LONG VARCHAR NOT NULL,
    encrypted_password LONG VARCHAR,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    disabled boolean NOT NULL DEFAULT false,
    mail_address LONG VARCHAR,
    CONSTRAINT login_user_pkey PRIMARY KEY (id)
)
