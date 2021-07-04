# simple-health-log

2021-07-04

## 概要

* 起床時刻
* 体温（摂氏）（朝夕）
* 酸素飽和度（朝夕）
* 就寝時刻

を記録する（だけ）のアプリ

## 必要なもの

* Gradle
* Jetty9.4+（推奨）
* PostgreSQL 12+

## 準備

### gradle.properties

* ~/.gradle/gralde.properties に以下を記載。必要に応じて適宜修正

```java
# development/productionおよびパスワード（**********）は必要に応じて変更
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
## for test(2021-07-04- )
jp.nauplius.app.shl.setting.test.db.url=jdbc:postgresql://localhost:5432/simple_health_log_test
jp.nauplius.app.shl.setting.test.db.user=healthlog
jp.nauplius.app.shl.setting.test.db.password=**********

#
# simple-health-log mail settings
#
jp.nauplius.app.shl.setting.mail.active=true
jp.nauplius.app.shl.setting.mail.smtp.host=localhost # or external server addr (eg smtp.gmail.com)
jp.nauplius.app.shl.setting.mail.smtp.port=25 (or 587, 465...not tested yet...)
# When authentication requered(eg. port=587), change below line to "true". And set userId, password.
jp.nauplius.app.shl.setting.mail.smtp.auth=false
jp.nauplius.app.shl.setting.mail.smtp.userId=**CHANGE THIS**
jp.nauplius.app.shl.setting.mail.smtp.password=**********


```

### PostgreSQL

* create user "healthlog". And create database.

```sql
CREATE USER healthlog PASSWORD '**********' NOINHERIT VALID UNTIL 'infinity';

-- test(2021-07-04- )
CREATE DATABASE simple_health_log_test WITH OWNER='healthlog';

-- development
-- CREATE DATABASE simple_health_log_development WITH OWNER='healthlog';

-- production
CREATE DATABASE simple_health_log_production WITH OWNER='healthlog';


```

### Build and Deploy war.

Run `gradle war` and deploy war.
