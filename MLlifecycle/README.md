# ML Lifecycle

GCS example From https://cloud.google.com/ml-engine/docs/distributed-tensorflow-mnist-cloud-datalab
BQ example from https://codelabs.developers.google.com/codelabs/dataeng-machine-learning/index.html?index=..%2F..%2Findex#3

##Data coming from GCS

Download the example files and set your current directory.

git clone https://github.com/GoogleCloudPlatform/cloudml-dist-mnist-example
cd cloudml-dist-mnist-example

BUCKET="whewatt-sandbox-ml"

NOTE:  The create_records.py is not working on Mac, whewatt-sandbox or whewatt-demo cloud shells.
It only worked on weshewatt@gmail.com cloud shell

./scripts/create_records.py
gsutil cp /tmp/data/train.tfrecords gs://${BUCKET}/data/
gsutil cp /tmp/data/test.tfrecords gs://${BUCKET}/data/

###Train
JOB_NAME="job_$(date +%Y%m%d_%H%M%S)"
gcloud ml-engine jobs submit training ${JOB_NAME} \
    --package-path trainer \
    --module-name trainer.task \
    --staging-bucket gs://${BUCKET} \
    --job-dir gs://${BUCKET}/${JOB_NAME} \
    --runtime-version 1.2 \
    --region us-central1 \
    --config config/config.yaml \
    -- \
    --data_dir gs://${BUCKET}/data \
    --output_dir gs://${BUCKET}/${JOB_NAME} \
    --train_steps 10000
	

###Deploy the model
MODEL_NAME=MNIST
gcloud ml-engine models create --regions us-central1 ${MODEL_NAME}
VERSION_NAME=v1
ORIGIN=$(gsutil ls gs://${BUCKET}/${JOB_NAME}/export/Servo | tail -1)
gcloud ml-engine versions create \
    --origin ${ORIGIN} \
    --model ${MODEL_NAME} \
    ${VERSION_NAME}
gcloud ml-engine versions set-default --model ${MODEL_NAME} ${VERSION_NAME}


###Test the Model API with 10 images
gcloud ml-engine predict --model ${MODEL_NAME} --json-instances request.json


In the output, CLASS is the number that the model thinks is represented by the image.

###Exec from Datalab
Start the datalabvm
Start VPN
datalab connect --zone us-east1-c datalabvm-033017
http://localhost:8081
Open the correct notebook and draw a number.

##Data from BigQuery
###Setup
Start the datalabvm
Start VPN
datalab connect --zone us-east1-c datalabvm-033017
http://localhost:8081

Clone https://github.com/GoogleCloudPlatform/training-data-analyst


##Call ML model from API
See http://localhost:8081/notebooks/datalab/training-data-analyst/courses/machine_learning/cloudmle/cloudmle.ipynb#

from googleapiclient import discovery
from oauth2client.client import GoogleCredentials
import json

credentials = GoogleCredentials.get_application_default()
api = discovery.build('ml', 'v1', credentials=credentials,
            discoveryServiceUrl='https://storage.googleapis.com/cloud-ml/discovery/ml_v1_discovery.json')

request_data = {'instances':
  [
      {
        'pickuplon': -73.885262,
        'pickuplat': 40.773008,
        'dropofflon': -73.987232,
        'dropofflat': 40.732403,
        'passengers': 2,
      }
  ]
}

parent = 'projects/%s/models/%s/versions/%s' % (PROJECT, 'taxifare', 'v1')
response = api.projects().predict(body=request_data, name=parent).execute()
print "response={0}".format(response)