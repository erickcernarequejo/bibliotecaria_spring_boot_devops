#!/bin/bash
# Detiene la aplicación Spring Boot en ejecución

# Verifica si el archivo PID existe
if [ -f /home/ec2-user/app/app.pid ]; then
    PID=$(cat /home/ec2-user/app/app.pid)
    echo "Stopping application with PID: $PID"
    kill $PID
    # Espera hasta que el proceso termine
    while kill -0 $PID 2>/dev/null; do
        sleep 1
    done
    echo "Application stopped"
    rm /home/ec2-user/app/app.pid
else
    echo "Application is not running"
fi