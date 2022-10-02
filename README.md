# simple-health-log

(Pardon my broken English.😅)

Updated at 2022-10-02

* Shows notification message when previous day's record not entered. (Notification content can be changed on the setting screen.)
* Addes some icons.😄

---

## Docker(2022-08-22～)

* See [README-Docker.md](docker/README-Docker.md)

---

## Summary

This app is recording only (below)..

* Awake time
* Body Temperatre(morning/evening)
* Oxygen saturation(morning/evening)
* Bed time
* Condition notes(Free style)

## Requirement

* JDK 1.8
* Gradle 7.1+
* Jetty9.4+（Recommended）
* PostgreSQL 12+

## Preparations

### Set www owner's home directory(2022-08-11～）

* (Unix/Linux)When www owner's(=service execution user) is '/nonexistent', create the owner's home directory(eg. "mkdir /home/www; chown www:www /home/www;" and change /etc/password for example by using the 'chsh' command.
* Only when starting for the first time, '~/simple-health-log/config.yml' is automatically created.
    * **Important** Save backup file in case of crash. If config.yml lost, Currently (and probably in the future) there is no recovery method.
    * When crashed, restore config.yml at www owner's '~/simple-health-log/' directory.

### PostgreSQL

* Create user "healthlog". And create database.
* Change '**～' to appropriate password (set same value as gradle.properties).
* As needed, change 'postgresql.conf' and 'pg_hba.conf'.

```sql
CREATE USER healthlog PASSWORD '**********' NOINHERIT VALID UNTIL 'infinity';

-- test
CREATE DATABASE simple_health_log_test WITH OWNER='healthlog' ENCODING = 'UTF8' CONNECTION LIMIT = -1;

-- development
-- CREATE DATABASE simple_health_log_development WITH OWNER='healthlog' ENCODING = 'UTF8' CONNECTION LIMIT = -1;

-- production
CREATE DATABASE simple_health_log_production WITH OWNER='healthlog' ENCODING = 'UTF8' CONNECTION LIMIT = -1;

```

### gradle.properties

* Add below text to ~/.gradle/gralde.properties.

```java
# Change '**********' to appropriate password.
#
# simple-health-log DB settings
#
jp.nauplius.app.shl.setting.dbType=production
# jp.nauplius.app.shl.setting.dbType=development
## for production
jp.nauplius.app.shl.setting.production.db.url=jdbc:postgresql://localhost:5432/simple_health_log_production
jp.nauplius.app.shl.setting.production.db.user=healthlog
jp.nauplius.app.shl.setting.production.db.password=**********
## for development
jp.nauplius.app.shl.setting.development.db.url=jdbc:postgresql://localhost:5432/simple_health_log_development
jp.nauplius.app.shl.setting.development.db.user=healthlog
jp.nauplius.app.shl.setting.development.db.password=**********
## for test
jp.nauplius.app.shl.setting.test.db.url=jdbc:postgresql://localhost:5432/simple_health_log_test
jp.nauplius.app.shl.setting.test.db.user=healthlog
jp.nauplius.app.shl.setting.test.db.password=**********

#
# simple-health-log mail settings
#
jp.nauplius.app.shl.setting.mail.active=false # to active, set 'true'.
jp.nauplius.app.shl.setting.mail.smtp.host=localhost # or external server addr (eg smtp.gmail.com)
jp.nauplius.app.shl.setting.mail.smtp.port=25 # or 587, 465 (...not tested yet...)
# When authentication requered(eg. port=587), change below line to "true". And set userId, password.
jp.nauplius.app.shl.setting.mail.smtp.auth=false
jp.nauplius.app.shl.setting.mail.smtp.userId=**CHANGE THIS**
jp.nauplius.app.shl.setting.mail.smtp.password=**********

```

### Build and Deploy war.

Run `gradle war` and deploy war.

## License

MIT

©2022 nauplius.jp

