#!/bin/sh

# PostgreSQL
# If exec in another host, comment out below 2 lines.
service postgresql start;
su postgres -c 'psql -f /etc/postgresql/12/createdb.sql -U postgres';

# Jetty
export JAVA_OPTIONS="-Dfile.encoding=UTF-8"

# To set default language as 'ja', uncomment below line.
# export JAVA_OPTIONS="${JAVA_OPTIONS} -Duser.language=ja"

/usr/share/jetty9/bin/jetty.sh status

/usr/share/jetty9/bin/jetty.sh start

echo "http://`hostname -i`:8080/simple-health-log"
exec "$@"

