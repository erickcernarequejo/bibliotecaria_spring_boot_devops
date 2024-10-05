#!/bin/bash
# Prepara el entorno para la nueva versión de la aplicación

# Actualiza el sistema
sudo yum update -y

# Instala Java si no está instalado
if ! command -v java &> /dev/null; then
    sudo yum install java-21-amazon-corretto-devel -y
fi

# Crea el directorio de la aplicación si no existe
mkdir -p /home/ec2-user/app

# Limpia el directorio de la aplicación
rm -rf /home/ec2-user/app/*

# Ajusta los permisos
#sudo chown -R ec2-user:ec2-user /home/ec2-user/app