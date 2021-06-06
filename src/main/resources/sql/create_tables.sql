--
-- PostgreSQL database dump
--

-- Dumped from database version 12.1
-- Dumped by pg_dump version 13.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: key_iv; Type: TABLE; Schema: public; Owner: healthlog
--

CREATE TABLE public.key_iv (
    id integer NOT NULL,
    encryption_key text,
    encryption_iv text,
    created_by integer,
    created_date timestamp with time zone,
    modified_by integer,
    modified_date timestamp with time zone
);


ALTER TABLE public.key_iv OWNER TO healthlog;

--
-- Name: physical_condition; Type: TABLE; Schema: public; Owner: healthlog
--

CREATE TABLE public.physical_condition (
    id integer NOT NULL,
    recording_date character varying(8) NOT NULL,
    awake_time time without time zone,
    bed_time time without time zone,
    body_temperature_morning numeric(3,1),
    body_temperature_evening numeric(3,1),
    oxygen_saturation_morning integer,
    oxygen_saturation_evening integer,
    condition_note text,
    created_by integer,
    created_date timestamp with time zone,
    modified_by integer,
    modified_date timestamp with time zone
);


ALTER TABLE public.physical_condition OWNER TO healthlog;

--
-- Name: user_info; Type: TABLE; Schema: public; Owner: healthlog
--

CREATE TABLE public.user_info (
    id integer NOT NULL,
    login_id text,
    encrypted_password text,
    name text,
    role_id integer DEFAULT 1,
    mail_address text,
    status integer DEFAULT 0,
    deleted boolean DEFAULT false,
    deleted_date timestamp with time zone,
    created_by integer,
    created_date timestamp with time zone,
    modified_by integer,
    modified_date timestamp with time zone
);


ALTER TABLE public.user_info OWNER TO healthlog;

--
-- Name: user_info_id_seq; Type: SEQUENCE; Schema: public; Owner: healthlog
--

CREATE SEQUENCE public.user_info_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_info_id_seq OWNER TO healthlog;

--
-- Name: user_info_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: healthlog
--

ALTER SEQUENCE public.user_info_id_seq OWNED BY public.user_info.id;


--
-- Name: user_token; Type: TABLE; Schema: public; Owner: healthlog
--

CREATE TABLE public.user_token (
    id integer NOT NULL,
    token text NOT NULL
);


ALTER TABLE public.user_token OWNER TO healthlog;

--
-- Name: user_info id; Type: DEFAULT; Schema: public; Owner: healthlog
--

ALTER TABLE ONLY public.user_info ALTER COLUMN id SET DEFAULT nextval('public.user_info_id_seq'::regclass);


--
-- Name: key_iv key_iv_pkey; Type: CONSTRAINT; Schema: public; Owner: healthlog
--

ALTER TABLE ONLY public.key_iv
    ADD CONSTRAINT key_iv_pkey PRIMARY KEY (id);


--
-- Name: physical_condition physical_condition_pkey; Type: CONSTRAINT; Schema: public; Owner: healthlog
--

ALTER TABLE ONLY public.physical_condition
    ADD CONSTRAINT physical_condition_pkey PRIMARY KEY (id, recording_date);


--
-- Name: user_info user_info_pkey; Type: CONSTRAINT; Schema: public; Owner: healthlog
--

ALTER TABLE ONLY public.user_info
    ADD CONSTRAINT user_info_pkey PRIMARY KEY (id);


--
-- Name: user_token user_token_pkey; Type: CONSTRAINT; Schema: public; Owner: healthlog
--

ALTER TABLE ONLY public.user_token
    ADD CONSTRAINT user_token_pkey PRIMARY KEY (id);


--
-- Name: physical_condition physical_condition_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: healthlog
--

ALTER TABLE ONLY public.physical_condition
    ADD CONSTRAINT physical_condition_id_fkey FOREIGN KEY (id) REFERENCES public.user_info(id);


--
-- Name: user_token user_token_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: healthlog
--

ALTER TABLE ONLY public.user_token
    ADD CONSTRAINT user_token_id_fkey FOREIGN KEY (id) REFERENCES public.user_info(id);


--
-- PostgreSQL database dump complete
--

