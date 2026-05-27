# Bibliotecaria - Sistema de Gestión de Libros con CI/CD

## 📖 Descripción
Sistema backend para gestión de libros y autores desarrollado con Java 21, Spring Boot, Maven y PostgreSQL. Implementado con prácticas DevOps y pipeline CI/CD en AWS.

## 🚀 Características Principales
- CRUD completo para libros y autores
- Filtrado por identificador
- Pipeline CI/CD automatizado
- Despliegue en AWS EC2
- Base de datos PostgreSQL en RDS

## 🛠 Stack Tecnológico
- Java 21
- Spring Boot
- Maven
- PostgreSQL
- AWS Services:
    - EC2
    - RDS
    - CodePipeline
    - CodeBuild
    - CodeDeploy
    - S3
    - IAM
    - VPC
    - Systems Manager

## 🏗 Arquitectura CI/CD
![CI/CD Pipeline](pipeline-diagram.png)

1. **Source**: GitHub
2. **Build**: AWS CodeBuild
3. **Deploy**: AWS CodeDeploy
4. **Host**: AWS EC2

## 🔧 Configuración del Entorno

### Prerrequisitos
- Cuenta AWS
- Git
- Java 21
- Maven
- PostgreSQL

### Configuración Local
1. Clonar el repositorio
```bash
git clone [url-repositorio]
cd bibliotecaria
```

2. Configurar variables de entorno
```properties
DB_HOST=<your-db-host>
DB_PORT=5432
DB_NAME=bibliotecaria_db
DB_USERNAME=<your-username>
DB_PASSWORD=<your-password>
```

3. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

## 📚 Documentación Adicional
Para instrucciones detalladas de configuración, consultar:
- [Configuración AWS](docs/AWS_SETUP.md)
- [Configuración CI/CD](docs/CICD_SETUP.md)
- [Guía de Despliegue](docs/DEPLOYMENT.md)

## 🔐 Seguridad
Los detalles de configuración de seguridad y políticas IAM se encuentran en [SECURITY.md](docs/SECURITY.md)

## 📝 Licencia
Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para detalles.

## 👥 Contribuir
Las contribuciones son bienvenidas. Por favor, lee [CONTRIBUTING.md](CONTRIBUTING.md) para detalles sobre nuestro código de conducta y el proceso para enviar pull requests.