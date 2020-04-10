FROM openjdk:8-jdk-alpine
COPY target/gs-spring-boot-0.1.0.jar /app.jar
EXPOSE 8080/tcp
ENTRYPOINT ["java", "-jar", "/app.jar"]
