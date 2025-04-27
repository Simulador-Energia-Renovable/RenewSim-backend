# RenewSim Backend ☀️💨🌊

**RenewSim** es el backend de un simulador de energías renovables que permite calcular generación de energía, eficiencia, y retorno de inversión basado en fuentes solares, eólicas e hidroeléctricas.

[![Build Status](https://github.com/Simulador-Energia-Renovable/RenewSim-backend/actions/workflows/build.yml/badge.svg)](https://github.com/Simulador-Energia-Renovable/RenewSim-backend/actions)
[![Coverage](https://img.shields.io/badge/Coverage-94%25-brightgreen)](https://github.com/Simulador-Energia-Renovable/RenewSim-backend)

---

## 🚀 Tecnologías utilizadas

- Java 21
- Spring Boot 
- Spring Security
- Spring Data JPA
- JWT Authentication
- H2 Database (test)
- MySQL (producción)
- JaCoCo (Coverage)
- Maven

---

## 📦 Instalación y ejecución

1. Clona el repositorio:
   ```bash
   git clone https://github.com/Simulador-Energia-Renovable/RenewSim-backend.git
   cd RenewSim-backend
   ```

2. Configura tu base de datos en el archivo `.env` o `application.properties`.

3. Levanta la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## 🧪 Testing

- Para ejecutar los tests:
  ```bash
  ./mvnw test
  ```

- Para generar el reporte de cobertura de código con JaCoCo:
  ```bash
  ./mvnw verify
  ```
  El reporte se generará en:  
  `/target/site/jacoco/index.html`

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

