# Build stage (multi-arch)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /usr/local/service
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage (Debian/Ubuntu, multi-arch)
FROM eclipse-temurin:17-jre
WORKDIR /app
# fontes e libs pra PDF (iText etc.)
RUN apt-get update && apt-get install -y --no-install-recommends \
    fontconfig fonts-dejavu-core && \
    rm -rf /var/lib/apt/lists/*
COPY --from=build /usr/local/service/target/*.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]

