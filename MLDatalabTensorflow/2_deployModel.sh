MODEL_NAME="housing_prices"
MODEL_VERSION="v1"
#REPLACE this with the location of your model
MODEL_LOCATION="gs://whewatt-sandbox-ml/housing_distributed_171205_145922/output/export/Servo/1512486342728" 

#gcloud ml-engine versions delete ${MODEL_VERSION} --model ${MODEL_NAME} #Uncomment to overwrite existing version
#gcloud ml-engine models delete ${MODEL_NAME} #Uncomment to overwrite existing model
gcloud ml-engine models create ${MODEL_NAME} --regions $REGION
gcloud ml-engine versions create ${MODEL_VERSION} --model ${MODEL_NAME} --origin ${MODEL_LOCATION} --staging-bucket=$GCS_BUCKET
