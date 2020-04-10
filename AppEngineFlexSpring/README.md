# Build a spring cloud app and deploy it to App Engine flex

The guestbook code is from Ray Tsang's workshop.
Code at https://github.com/saturnism/spring-cloud-gcp-guestbook

## Build the project
    ./mvnw package

## Dockerfile contents

    FROM openjdk:8-jdk-alpine
    VOLUME /tmp
    COPY target/guestbook-0.0.1-SNAPSHOT.war app.jar
    ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

## Local development - build the container and tag it

docker build -t gcr.io/whewatt-sandbox/guestbook:1.0 .

## Local development - test the container

    docker run -p 8080:8080 gcr.io/whewatt-sandbox/guestbook:1.0
    Visit http://localhost:8080/guestbookMessages

## app.yaml contents

    runtime: custom
    env: flex
    service: flexible-guest

## Deploy to App Engine

    gcloud app deploy
    Visit http://flexible-guest-dot-whewatt-sandbox.appspot.com/guestbookMessages