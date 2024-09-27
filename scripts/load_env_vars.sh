#!/bin/bash

# Cargar variables desde AWS Systems Manager Parameter Store
export DB_NAME=$(aws ssm get-parameter --name "/DB/NAME" --with-decryption --query Parameter.Value --output text)
export DB_USERNAME=$(aws ssm get-parameter --name "/DB/USERNAME" --with-decryption --query Parameter.Value --output text)
export DB_PASSWORD=$(aws ssm get-parameter --name "/DB/PASSWORD" --with-decryption --query Parameter.Value --output text)
export DB_HOST_WRITER=$(aws ssm get-parameter --name "/DB/HOST/WRITER" --with-decryption --query Parameter.Value --output text)
export DB_SCHEMA=$(aws ssm get-parameter --name "/DB/SCHEMA" --with-decryption --query Parameter.Value --output text)
