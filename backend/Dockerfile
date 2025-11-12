# build 
FROM maven:3.8-openjdk-17 AS build
WORKDIR /usr/local/service
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#  runtime 
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# pdf libs
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      fontconfig \
      fonts-dejavu-core \
      libfreetype6 \
 && rm -rf /var/lib/apt/lists/*

# executar em modo headless
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"

COPY --from=build /usr/local/service/target/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
