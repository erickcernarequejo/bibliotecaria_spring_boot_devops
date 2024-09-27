#!/bin/bash
# Valida que la aplicación esté funcionando correctamente

# Espera a que la aplicación se inicie completamente
sleep 40

# Verifica si la aplicación está respondiendo
HEALTH_CHECK_URL="http://localhost:80/actuator/health"
MAX_RETRIES=2
RETRY_INTERVAL=5

for i in $(seq 1 $MAX_RETRIES); do
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_CHECK_URL)
    if [ $HTTP_STATUS -eq 200 ]; then
        echo "Application is healthy and responding"
        exit 0
    else
        echo "Attempt $i: Application is not responding. Retrying in $RETRY_INTERVAL seconds..."
        sleep $RETRY_INTERVAL
    fi
done

echo "Application failed to respond after $MAX_RETRIES attempts"
exit 1