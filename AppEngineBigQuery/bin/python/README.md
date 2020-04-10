# App Engine Standard Environment BigQuery Sample

This sample demonstrates how to deploy an application on Google App Engine that inserts values into BigQuery

###This  directory contains an incomplete Python sample.  Specifically, when deployed to GAE, an erorr is generated trying to import the ctypes library.

Python Samples:
https://github.com/GoogleCloudPlatform/python-docs-samples/tree/master/appengine/standard/bigquery
https://github.com/GoogleCloudPlatform/python-docs-samples/blob/master/appengine/standard/README.md
https://github.com/GoogleCloudPlatform/google-cloud-python/tree/master/bigquery

## Setup

Use either:

* `gcloud init`
* `gcloud beta auth application-default login`

## Run and Deploy
### Import Python Libraries

    $ pip install -t lib -r requirements.txt

This imports the libaries listed in the text file and puts them into the lib subdirectory.
The code in appengine_config.py loads the libraries when executed inside GAE.

I believe the library import can also be handled by adding the libraries to app.yaml, as we did in AppEnginePythonCSSI.

### Running locally {NOT TESTED}

    $ dev_appserver.py main.py

### Deploying

    $ gcloud app deploy



## Using the app

    https://{gae url}?value={something to insert}

