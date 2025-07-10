# RenewSim Backend ☀️🀈🌊

## 📑 Tabla de Contenidos

- [Descripción](#-descripción)
- [Tecnologías utilizadas](#-tecnologías-utilizadas)
- [Instalación y ejecución](#-instalación-y-ejecución)
- [Testing](#-testing)
- [CD con GitHub Actions](#-cd-con-github-actions)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Endpoints principales](#-endpoints-principales)
- [Autor](#-autor)
- [Licencia](#-licencia)

---

## 🧾 Descripción

**RenewSim** es el backend de un simulador de energías renovables que permite calcular generación, eficiencia y retorno de inversión de proyectos basados en energía solar, eólica e hidroeléctrica. Desarrollado en Java 17 + Spring Boot, el proyecto cuenta con autenticación segura, arquitectura limpia, cobertura de pruebas y despliegue automático.

[![Build Status](https://github.com/Simulador-Energia-Renovable/RenewSim-backend/actions/workflows/build.yml/badge.svg)](https://github.com/Simulador-Energia-Renovable/RenewSim-backend/actions)
[![Coverage](https://img.shields.io/badge/Coverage-94%25-brightgreen)](https://github.com/Simulador-Energia-Renovable/RenewSim-backend)

---

## 🚀 Tecnologías utilizadas

- Java 17 / Java 21
- Spring Boot · Spring Security · Spring Data JPA
- JWT Authentication con Keycloak
- MySQL (producción) · H2 (test)
- JaCoCo (coverage) · Maven
- GitHub Actions (CI/CD)

### 🧪 Testing QA

- **JUnit 5** + **Mockito** para unit testing
- **Testcontainers** para tests de integración con MySQL real en Docker
- **Postman** para validación de endpoints REST
- **Selenium (Java)** para pruebas E2E desde frontend conectando a esta API

---

## 📦 Instalación y ejecución

```bash
git clone https://github.com/Simulador-Energia-Renovable/RenewSim-backend.git
cd RenewSim-backend
```

2. Configura tu base de datos en el archivo `.env` o `application.properties`.
  Edita src/main/resources/application.properties o utiliza un archivo .env.

4. Levanta la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## 🧪 Testing

- Para ejecutar los tests:
  ```bash
  ./mvnw test
  ```
  
▶️ Tests con Testcontainers:

  ```bash
  ./mvnw verify -Dspring.profiles.active=testcontainers

  ```
Requiere Docker corriendo.

- Para generar el reporte de cobertura de código con JaCoCo:
  ```bash
  ./mvnw verify
  ```
  El reporte se generará en:  
  `/target/site/jacoco/index.html`

  🧪 Selenium
Pruebas E2E automatizadas con Selenium (Java)

Scripts disponibles en /docs/selenium/

Ejecutan flujos de login → simulación → validación

---

## 🚀 CD - Build and Package Spring Boot App

Este workflow se encarga de construir y empaquetar el backend de RenewSim automáticamente cuando se realiza un push a la rama `main`.

### 📋 Descripción del flujo

1. **Checkout del repositorio**: Clona el proyecto en el runner de GitHub Actions.
2. **Configuración de Java**: Instala y configura Java 17 (usando Temurin).
3. **Build del proyecto**: Ejecuta `mvn clean package -DskipTests` para empaquetar la aplicación en un archivo `.jar`.
4. **Upload del artefacto**: Sube el archivo `.jar` generado al apartado de Artifacts en GitHub.

### 🛠️ Tecnologías y herramientas

- GitHub Actions
- Java 17
- Apache Maven
- Spring Boot
- Artifacts de GitHub

### 📂 Archivo de configuración `.github/workflows/deploy.yml`

```yaml
name: 🚀 CD - Build and Package Spring Boot App

on:
  push:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: 📥 Checkout repository
        uses: actions/checkout@v3

      - name: ☕ Set up Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: 📦 Build with Maven
        run: mvn clean package -DskipTests

      - name: 📂 Upload JAR artifact
        uses: actions/upload-artifact@v3
        with:
          name: renewsim-backend
          path: target/*.jar
```

### ⚠️ Notas importantes

- El despliegue se activa **solo cuando se hace `push` o `merge` a `main`**.
- El archivo `.jar` generado estará disponible para descargar en la sección **Artifacts** de GitHub Actions.
- Actualmente los tests **no se ejecutan** en este flujo para acelerar la construcción (`-DskipTests`).

---

## 📂 Estructura del proyecto

```bash
src/main/java/com/renewsim/backend
├── config          # Configuración de seguridad y base de datos
├── exception       # Manejo de excepciones globales
├── role            # Gestión de roles de usuario
├── simulation      # Lógica de simulaciones de energía renovable
├── user            # Gestión de usuarios
└── security        # Seguridad OAuth2 y JWT
```

---

## 🌐 Endpoints principales

| Método | Ruta                          | Descripción                     |
|--------|-------------------------------|----------------------------------|
| POST   | `/api/v1/auth/login`           | Login de usuario                |
| POST   | `/api/v1/auth/register`        | Registro de usuario             |
| GET    | `/api/v1/users/me`             | Datos del usuario autenticado   |
| PUT    | `/api/v1/users/change-password`| Cambiar contraseña              |
| GET    | `/api/v1/simulation/user`      | Historial de simulaciones       |
| POST   | `/api/v1/simulation`           | Crear nueva simulación          |

---

## 👩‍💻 Autor

Proyecto desarrollado por [Lanny Rivero Canino](https://www.linkedin.com/in/lannyriverocanino/).

---

## 📜 Licencia

Este proyecto está bajo la licencia [MIT](LICENSE).

---

