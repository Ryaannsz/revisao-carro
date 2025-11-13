# Etapa 1: build
FROM maven:3.8-eclipse-temurin-17 AS build

WORKDIR /usr/local/service

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /usr/local/service/target/*.jar /app/app.jar

# Porta padrão Spring Boot
EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]

