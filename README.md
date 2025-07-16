# Bibliotecaria Spring Boot DevOps

Solución de backend para gestionar libros y autores, utilizando Spring Boot y prácticas DevOps para el flujo CI/CD.  

---

## 🚀 Descripción

Este proyecto implementa un servicio CRUD para la gestión de autores y libros. Está diseñado para ser fácilmente desplegado y actualizado mediante integración y despliegue continuo (CI/CD) en AWS, haciendo uso de scripts y herramientas de automatización modernas.

---

## 🛠️ Tecnologías utilizadas

- **Java 21 (JDK 21)**
- **Spring Boot**
- **Maven**
- **PostgreSQL** (como base de datos)
- **AWS CodeBuild**, **AWS CodeDeploy**, **AWS CodePipeline**, **AWS Parameter**, **AWS RDS Postgres** (para CI/CD)
- **Shell Scripts** (automatización de despliegue, ubicados en la carpeta `/scripts`)

---

## 📦 Instalación y requisitos previos

1. **JDK 21** instalado en el sistema.
2. **Maven** instalado.
3. **PostgreSQL** en ejecución y accesible.
4. Acceso a una cuenta de **AWS** con servicios de CodeBuild, CodeDeploy y CodePipeline habilitados.

---

## ⚙️ Ejecución local

```bash
# Clona el repositorio
git clone https://github.com/erickcernarequejo/bibliotecaria_spring_boot_devops.git
cd bibliotecaria_spring_boot_devops

# Compila y ejecuta la aplicación
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en:  
- `http://localhost:8080/authors`  
- `http://localhost:8080/books`

---

## ☁️ Despliegue en AWS (CI/CD)

Los archivos dentro de la carpeta `/scripts` están diseñados para facilitar la integración y el despliegue continuo en AWS, utilizando:

- **AWS CodeBuild**: Compilación y pruebas automáticas.
- **AWS CodeDeploy**: Despliegue automatizado en la infraestructura deseada.
- **AWS CodePipeline**: Orquestación de todo el flujo CI/CD.

### Pasos generales:

1. Configura los servicios de AWS mencionados.
2. Ajusta los scripts según tu entorno y necesidades.
3. Sube el código al repositorio para activar el pipeline y desplegar automáticamente.

### 🎥 Vídeos demostrativos

- [Despliegue CI/CD en AWS - Parte 1](https://youtu.be/BR5dMoPYxxs)
- [Despliegue CI/CD en AWS - Parte 2](https://youtu.be/LmzQMjPmaZE)

---

## 📑 Endpoints principales

- **`GET /authors`**, **`POST /authors`**, **`PUT /authors/{id}`**, **`DELETE /authors/{id}`**
- **`GET /books`**, **`POST /books`**, **`PUT /books/{id}`**, **`DELETE /books/{id}`**

---

## 🤝 Contribuciones

¡Las contribuciones son bienvenidas! Por favor, abre un issue o pull request para sugerencias, mejoras o corrección de errores.

---

## 📄 Licencia

Este proyecto está bajo la licencia MIT.

---

<div align="right">Hecho con ❤️ por Erick Cerna</div>