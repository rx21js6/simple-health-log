# Docker

2022-08-22

## Using

* Ubuntu 20.04
* PostgreSQL 12
* Jetty 9.4
* JDK11
* Gradle 7.5.1

## Requirement

* Linux + Docker (Checked on Ubuntu 20.04 / Docker 20.10.14)
* Other tools required for build, is automatically loaded (in Dockerfile).

## Preparations

* Edit below files properly.
* Place all files at the directory where 'Dockerfile' exists.

### entrypoint.sh

Default language is 'en' (maybe...😅). To set default language as 'ja', uncomment below line.

```TXT
# export JAVA_OPTIONS="${JAVA_OPTIONS} -Duser.language=ja"
```

### createdb.sql

* Change '\*\*\*\*\*\*\*\*\*\*' to appropriate password.
* To use remote DB server, Run this SQL at the remote manually.

### gradle.properties

* Database
    * By default, used localhost server.
    * Change '\*\*\*\*\*\*\*\*\*\*' to appropriate password. (Set same password as 'createdb.sql'.)
* E-mail
    * By default, E-mail server is not active. To activate, change ```jp.nauplius.app.shl.setting.mail.active``` to 'true',  and set host, port, auth, userId and password properly.

## Building Image

Run "docker image build" command. (Set image name freely. below is example.)

```TXT
# docker image build -t simple-health-log/simple-health-log .
```

When finished. Run ``` # docker images ``` and check image is created.

## Run

At the directory exists Dockerfile(and others), run ```docker run -it IMAGENAME  /bin/bash``` command. (below is example.)

```
# docker run -it simple-health-log/simple-health-log /bin/bash
```

When first boot, entrypoint.sh is executed.


## Reboot

### start

If container is shutdown, run command below. (CONTAINER ID is changed dynamically.)

```TXT
# docker ps -a
CONTAINER ID   IMAGE                                 COMMAND                  CREATED          STATUS          PORTS     NAMES
NNNNNNNNNN   simple-health-log/simple-health-log   "/entrypoint.sh /bin…"   53 minutes ago   Up 53 minutes             sad_wiles

# docker start NNNNNNNNNN

```

### exec

To access container using shell, run command below.(CONTAINER ID is changed dynamically.)

```TXT
# docker ps -a
CONTAINER ID   IMAGE                                 COMMAND                  CREATED          STATUS          PORTS     NAMES
NNNNNNNNNN   simple-health-log/simple-health-log   "/entrypoint.sh /bin…"   53 minutes ago   Up 53 minutes             sad_wiles

# docker exec -it NNNNNNNNNN /bin/bash

```

### Restarting Jetty

Run below command.

```TXT
To set language as 'ja', set ENV before starting.
# export JAVA_OPTIONS="${JAVA_OPTIONS} -Duser.language=ja"

Restart command
# /usr/share/jetty9/bin/jetty.sh stop
# /usr/share/jetty9/bin/jetty.sh start
```

## Access

Access below URL.

```
http://hostname:8080/simple-health-log
```

## Backup

* config.yml
    * Saved at '''/root/simple-health-log/config.yml'''. Don't forget backup.
* DB
    * Original tools is not prepared. Use pg_dump/pg_dumpall command.(To see usage of these commands, refer to PostgreSQL web page.)

## Upgrading

* T.B.D.

