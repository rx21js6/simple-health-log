-- DROP DATABASE simple_health_log_production;

CREATE DATABASE simple_health_log_production
    WITH
    OWNER = healthlog
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;
