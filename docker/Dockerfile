FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive

ARG pg_dir=/etc/postgresql/12
ARG jetty_dir=/usr/share/jetty9

# Install apps
RUN apt-get update && apt-get install -y \
unzip \
git \
postgresql-12 \
openjdk-8-jdk \
jetty9 \
wget

WORKDIR ${jetty_dir}/workspace/
RUN chown jetty:jetty ${jetty_dir}/workspace/
WORKDIR ${jetty_dir}/.gradle/
RUN chown jetty:jetty ${jetty_dir}/.gradle/

USER jetty

# Gradle
WORKDIR ${jetty_dir}/workspace/
RUN wget -c https://services.gradle.org/distributions/gradle-7.5.1-bin.zip
RUN unzip gradle-7.5.1-bin.zip
RUN chmod 700 ${jetty_dir}/workspace/gradle-7.5.1/bin/gradle
ENV PATH ${jetty_dir}/workspace/gradle-7.5.1/bin:$PATH

# Git
WORKDIR ${jetty_dir}/workspace/
RUN git clone https://github.com/rx21js6/simple-health-log.git

# Gradle
COPY gradle.properties ${jetty_dir}/.gradle/
WORKDIR ${jetty_dir}/workspace/simple-health-log/
RUN gradle war

# simple-health-log deploy
RUN cp ${jetty_dir}/workspace/simple-health-log/build/libs/simple-health-log.war ${jetty_dir}/webapps/

# simpla-health-log createdb
USER postgres
WORKDIR ${pg_dir}
COPY createdb.sql ${pg_dir}

# simple-health-log config
USER root
RUN mkdir ${jetty_dir}/simple-health-log/
RUN chown jetty:jetty ${jetty_dir}/simple-health-log/

# jetty JAVA_OPTIONS
USER root
RUN echo 'export JAVA_OPTIONS="-Dfile.encoding=UTF-8"' >> ~/.bashrc

# entrypoint
USER root
WORKDIR /
COPY entrypoint.sh /
RUN chmod 700 /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

