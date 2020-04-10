# Rec Serve

This project deploys a solution for a recommendation service on GCP, using the WALS
algorithm in TensorFlow.  Components include:

- Recommendation model code, and scripts to train and tune the model on ML Engine
- A REST endpoint using [Google Cloud Endpoints](https://cloud.google.com/endpoints/) for serving recommendations
- An Airflow server on GKE for running a scheduled model training task


## Before you begin

1. Create a new [Cloud Platform project](https://console.cloud.google.com/projectcreate).

2. [Enable billing](https://support.google.com/cloud/answer/6293499#enable-billing)
   for your project.

3. [Enable APIs](https://console.cloud.google.com/apis/dashboard) for
  * ML Engine
  * App Engine Admin
  * Container Engine
  * Cloud SQL API


## Installation

### Option 1: Use Google Cloud Shell

1. Open the [Google Cloud Platform
   Console](https://console.cloud.google.com/?_ga=1.38191587.1500870598.1489443487).

2. Click the Cloud Shell icon at the top of the screen.
![Cloud Shell](https://cloud.google.com/shell/docs/images/shell_icon.png)

### Option 2: Run Locally in Linux or Mac OS X

*These scripts will not work in Windows. If you have a Windows machine, we
recommend you use Google Cloud Shell.*

1.  Download and install the [Google Cloud
    SDK](https://cloud.google.com/sdk/docs/), which includes the
    [gcloud](https://cloud.google.com/sdk/gcloud/) command-line tool.

2.  Initialize the Cloud SDK.

        gcloud init

3.  Set your default project (replace YOUR-PROJECT-ID with the name of your
    project).

        gcloud config set project YOUR-PROJECT-ID

### Clone the project code to your local directory

        gcloud source repos clone rec_serve --project=lramsey-goog-com-csa-airflow
        cd rec_serve

### Install Miniconda 2

This project assumes Python 2.

* Install miniconda 2:

https://conda.io/docs/user-guide/install/index.html


* Create environment and install packages:

Install packages in requirements.txt:

    conda create -y -n recserve
    source activate recserve
    conda install -y -n recserve --file conda.txt

* Install TensorFlow.

CPU:

    pip install tensorflow

Or GPU, if one is available in your environment:

    pip install tensorflow-gpu

Install other requirements not available from conda:

    pip install -r requirements.txt

### Upload sample data to BigQuery

This tutorial comes with a sample Google Analytics data set, containing page tracking events from the Austrian news site Kurier.at.  The schema file ga_sessions_sample_schema.json and data file ga_sessions_sample.json.gz are located in the folder 'data' in the tutorial code.  To upload this data set to BigQuery:

1. Make a GCS bucket with the name recserve_<YOUR-PROJECT-ID>:

     export BUCKET=gs://recserve_$(gcloud config get-value project 2> /dev/null)
     gsutil mb ${BUCKET}

2. Copy the data file ga_sessions_sample.json.gz to the bucket:

	   gsutil -m cp data/ga_sessions_sample.json.gz ${BUCKET}/data/ga_sessions_sample.json.gz

3.  (Option 1) Go to the BigQuery web UI. Create a new dataset named "GA360_test". In the navigation panel, hover on the dataset, click the down arrow icon, and click Create new table. On the Create Table page, in the Source Data section:
      - For Location, select Google Cloud Storage, and enter the file path  [your_bucket]/data/ga_sessions_sample.json.gz (without the gs:// prefix).
      - For File format, select JSON.
      - On the Create Table page, in the Destination Table section, for Table name, choose the dataset, and in the table name field, enter the name of the table as 'ga_sessions_sample'.
      - Verify that Table type is set to Native table.
      - In the Schema section, enter the schema definition.
      - Open the file data/ga_sessions_sample_schema.json in a text editor, select all, and copy the complete text of the file to the clipboard. Click Edit as text and paste the table schema into the text field.
      - Click Create Table.


3.  (Option 2) Using the command line:

    export PROJECT=$(gcloud config get-value project 2> /dev/null)

    bq --project_id=${PROJECT} mk GA360_test

    bq load --source_format=NEWLINE_DELIMITED_JSON \
      GA360_test.ga_sessions_sample \
      ${BUCKET}/data/ga_sessions_sample.json.gz \
      data/ga_sessions_sample_schema.json


### Install WALS model training package and model data

1. Create a distributable package. Copy the package up to the code folder in the bucket you created previously.

    pushd wals_ml_engine
    python setup.py sdist
    gsutil cp dist/wals_ml_engine-0.1.tar.gz ${BUCKET}/code/

2. Run the wals model on the sample data set:

    ./mltrain.sh local ../data/recommendation_events.csv --data-type web_views --use-optimized

    This will take a couple minutes, and create a job directory under wals_ml_engine/jobs like "wals_ml_local_20180102_012345/model", containing the model files saved as numpy arrays.

3. Copy the model files from this directory to the model folder in the project bucket:

    export JOB_MODEL=$(find jobs -name "model" | tail -1)
    gsutil cp ${JOB_MODEL}/* ${BUCKET}/model/

4. Copy the sample data file up to the project bucket:

    gsutil cp ../data/recommendation_events.csv ${BUCKET}/data/
    popd

### Install the recserve endpoint

This step can take several minutes to complete, so do it in a separate shell.  That way you can deploy the Airflow service in parallel.  Remember to 'source activate recserve' in any new shell that you open, to activate the recserve envionment.

    source activate recserve
    gcloud app create --region=us-east1
    gcloud app update --no-split-health-checks
    pushd scripts
    ./deploy_api.sh    # Deploy the API.
    ./deploy_app.sh    # Deploy the backend app.
    popd


### Deploy the Airflow service

This can be done in parallel with the previous step in a different shell.

1. Deploy the Airflow service using the script in airflow/deploy:

    pushd airflow/deploy
    ./deploy_airflow.sh
    popd

This will take a few minutes to complete.

2. Create "dags," "logs" and "plugins" folders in the GCS bucket created by the deploy script named (managed-airflow-{random hex value}), e.g. gs://managed-airflow-e0c99374808c4d4e8002e481. See https://screenshot.googleplex.com/zHuLCCDZ1GU.png.  The name of the bucket is available in the ID field of the airflow/deploy/deployment-settings.yaml file created by the deploy script.  You can create the folders in the cloud console, or use the following script:

    python airflow/deploy/create_buckets.py

3. Copy training.py to the dags folder in your airflow bucket:

    export AIRFLOW_BUCKET=`python -c "\
    import yaml;\
    f = open('airflow/deploy/deployment-settings.yaml');\
    settings=yaml.load(f);\
    f.close();\
    print settings['id']"`\

    gsutil cp airflow/dags/training.py gs://${AIRFLOW_BUCKET}/dags

4. Copy plugins to the plugins folder of your airflow bucket:

    gsutil cp -r airflow/plugins gs://${AIRFLOW_BUCKET}

5. Restart the airflow webserver pod

    WS_POD=`kubectl get pod | grep -o airflow-webserver-[0-9]*-[0-9a-z]*`
    kubectl get pod ${WS_POD} -o yaml | kubectl replace --force -f -



## Usage

### Airflow

You can find the URL and login credentials for the airflow admin interface in the file
airflow/deploy/deployment-settings.yaml.

e.g.

    ...
    web_ui_password: IiDYrpwJcT...
    web_ui_url: http://35.226.101.220:8080
    web_ui_username: airflow

The Airflow service can also be accessed from the airflow-webserver pod in the GKE cluster.
Open your project console, navigate to the "Discovery and load balancing" page in GKE, and
click on the endpoint link for the airflow-webserver to access the Airflow admin app.


### rec_serve endpoint service

    cd scripts
    ./query_api.sh          # Query the API.
    ./generate_traffic.sh   # Send traffic to the API.


