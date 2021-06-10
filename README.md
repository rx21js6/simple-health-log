# simple-health-log

## 概要

* 起床時刻
* 体温（摂氏）（朝夕）
* 酸素飽和度（朝夕）
* 就寝時刻

を記録する（だけ）のアプリ

## 必要なもの

* Gradle
* Jetty（推奨）
* PostgreSQL(12+)

## 準備

### gradle.properties

~/.gradle/gralde.properties に以下を記載。必要に応じて適宜修正

```java
jp.nauplius.app.shl.setting.production.dbUrl=jdbc:postgresql://localhost:5432/simple_health_log_production
jp.nauplius.app.shl.setting.production.dbUser=healthlog
jp.nauplius.app.shl.setting.production.dbPassword=**********
jp.nauplius.app.shl.setting.development.dbUrl=jdbc:postgresql://localhost:5432/simple_health_log_development
jp.nauplius.app.shl.setting.development.dbUser=healthlog
jp.nauplius.app.shl.setting.development.dbPassword=**********
```

### PostgreSQL

create user "healthlog". And set "CREATEDB".

```sql
CREATE USER healthlog PASSWORD '**********' CREATEDB;
```

### Build and Deploy war.

Run `gradle war` and deploy war.
