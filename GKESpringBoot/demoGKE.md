mvn package

java -jar target/gs-spring-boot-0.1.0.jar

docker build -t gcr.io/whewatt-sandbox/hello:1.0 .

docker run -it --rm -p 8000:8080 gcr.io/whewatt-sandbox/hello:1.0

gcloud docker -- push gcr.io/whewatt-sandbox/hello:1.0

gcloud container clusters get-credentials NAME --zone=ZONE --project=PROJECT

kubectl run hello --image=gcr.io/whewatt-sandbox/hello:1.0 --replicas=3 --port 8080

kubectl expose deployment hello --type=LoadBalancer --port 8080

kubectl get service

kubectl delete service hello
kubectl delete deployment hello
docker rmi gcr.io/whewatt-sandbox/hello:1.0