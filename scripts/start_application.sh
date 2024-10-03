#!/bin/bash

# Carga las variables de entorno
source /home/ec2-user/app/scripts/load_env_vars.sh

# Directorio de la aplicación
APP_DIR="/home/ec2-user/app"
JAR_DIR="$APP_DIR/target"
LOG_DIR="$APP_DIR/logs"

# Nombre del archivo JAR (asume que solo hay uno)
JAR_FILE=$(ls $JAR_DIR/*.jar | head -n 1)

nohup java -jar \
      -Dserver.port=8080 \
      -DDB_NAME=$DB_NAME \
      -DDB_USERNAME=$DB_USERNAME \
      -DDB_PASSWORD=$DB_PASSWORD \
      $JAR_FILE > /dev/null 2>&1 &

echo $! > ./app.pid