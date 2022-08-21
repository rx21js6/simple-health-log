-- Change '**********' to reasonable password.
CREATE USER healthlog PASSWORD '***********' NOINHERIT VALID UNTIL 'infinity';

CREATE DATABASE simple_health_log_production WITH OWNER='healthlog' ENCODING = 'UTF8' CONNECTION LIMIT = -1;
