# Etapa de compilación
FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .
RUN mvn clean package

# Etapa de ejecución
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar backend-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]


