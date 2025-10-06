# build 
FROM maven:3.8-openjdk-17 AS build
WORKDIR /usr/local/service
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

#  runtime 
FROM openjdk:17-jdk-alpine
WORKDIR /app

# pdf libs
RUN apk add --no-cache freetype fontconfig ttf-dejavu

# executar em modo headless
ENV JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"

COPY --from=build /usr/local/service/target/*.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
