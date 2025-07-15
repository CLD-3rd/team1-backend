FROM openjdk:17

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

COPY src/main/resources/application.properties /app/resources/application.properties
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.config.location=file:/app/resources/application.properties"]
