#!/bin/bash
# Ajusta los permisos de todo el directorio de la aplicación
sudo chown -R ec2-user:ec2-user /home/ec2-user/app

# Navega al directorio de la aplicación
cd /home/ec2-user/app/target

# Ajusta los permisos del JAR
chmod +x *.jar

echo "Configuración de la aplicación actualizada correctamente."