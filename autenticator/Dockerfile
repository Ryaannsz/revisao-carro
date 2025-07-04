FROM maven:3.8-openjdk-17 AS build

WORKDIR /usr/local/service
COPY pom.xml /usr/local/service/pom.xml
COPY src /usr/local/service/src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /usr/local/service/target/*.jar /app/app.jar

ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
