## Build the app

    mvn package

## Run the app

    java -jar target/gs-spring-boot-0.1.0.jar

## Build the container

    docker build -t gcr.io/whewatt-sandbox/hello-java:1.0 .

## Run the container locally

    docker run -it --rm -p 8080:8080 gcr.io/whewatt-sandbox/hello-java:1.0

## Deploy the container to App Engine

    gcloud app deploy

##  Deploy the container to GKE

    gcloud docker -- push gcr.io/whewatt-sandbox/hello-java:1.0
    
    gcloud container clusters create mycluster --num-nodes=3
    
    kubectl run hello-java --image=gcr.io/whewatt-sandbox/hello-java:1.0 --port 8080

    kubectl get pods 

    kubectl expose deployment hello-java --type=LoadBalancer --port=8080

    kubectl get service
