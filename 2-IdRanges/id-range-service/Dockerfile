FROM --platform=linux/amd64 openjdk:17-alpine

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

CMD ["java", "-jar", "app.jar"]