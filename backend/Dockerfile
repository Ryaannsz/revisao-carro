FROM openjdk:17-jdk-slim AS build

WORKDIR /app

RUN apt-get update && apt-get install -y maven

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

ENV DATABASE_URL=jdbc:postgresql://ep-shiny-queen-a4ugjq1f-pooler.us-east-1.aws.neon.tech:5432/neondb?sslmode=require
ENV PASSWORD=npg_oYp2EHcG6uVb
ENV USERNAME=neondb_owner

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
