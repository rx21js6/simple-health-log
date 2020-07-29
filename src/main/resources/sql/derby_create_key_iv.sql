CREATE TABLE key_iv
(
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY,
    encryption_key LONG VARCHAR,
    encryption_iv LONG VARCHAR,
    created_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
)
