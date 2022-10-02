CREATE TABLE IF NOT EXISTS public.not_entered_notice
(
    id integer NOT NULL,
    type_key text COLLATE pg_catalog."default" NOT NULL,
    message_id text COLLATE pg_catalog."default" NOT NULL,
    checked boolean NOT NULL DEFAULT true,
    created_by integer,
    created_date timestamp with time zone,
    modified_by integer,
    modified_date timestamp with time zone,
    CONSTRAINT not_entered_notice_pkey PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.not_entered_notice
    OWNER to healthlog;
