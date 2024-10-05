#!/bin/bash

# Carga las variables de entorno
source /home/ec2-user/app/scripts/load_env_vars.sh

# Directorio de la aplicación
APP_DIR="/home/ec2-user/app"
JAR_DIR="$APP_DIR/target"
LOG_DIR="$APP_DIR/logs"

# Nombre del archivo JAR (asume que solo hay uno)
JAR_FILE=$(ls $JAR_DIR/*.jar | head -n 1)

# Crea el directorio de logs si no existe
mkdir -p $LOG_DIR

# Archivo de log
LOG_FILE="$LOG_DIR/application.log"

nohup java -jar \
      -Dserver.port=8080 \
      -DDB_NAME=$DB_NAME \
      -DDB_USERNAME=$DB_USERNAME \
      -DDB_PASSWORD=$DB_PASSWORD \
      -Dlogging.file.path=$LOG_DIR \
      -Dlogging.file.name=$LOG_FILE \
      $JAR_FILE >> $LOG_FILE 2>&1 &

echo $! > /home/ec2-user/app/app.pid
