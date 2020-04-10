#Set required env vars
export DBAAS_USER_NAME=demowebsim
export DBAAS_USER_PASSWORD=websim321
export DBAAS_DEFAULT_CONNECT_DESCRIPTOR=127.0.01:1521/SIM.usatandttrial77.oraclecloud.internal

#Execute the app
java -jar target/springboot-helloworld.jar
