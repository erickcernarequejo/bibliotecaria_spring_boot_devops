#!/bin/bash
source /home/ec2-user/app/scripts/load_env_vars.sh
cd /home/ec2-user/app
nohup java -jar \
      -Dserver.port=80 \
      -DDB_NAME=$DB_NAME \
      -DDB_USERNAME=$DB_USERNAME \
      -DDB_PASSWORD=$DB_PASSWORD \
      *.jar > /dev/null 2>&1 &
echo $! > ./app.pid