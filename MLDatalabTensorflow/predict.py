#Based on code from taxifare demo.  See the ACL listings in that notebook 
#(training-data-analyst/courses/machine_learning/cloudmle/cloudmle.ipynb) if
#security errors occur.
from oauth2client.client import GoogleCredentials
from googleapiclient import discovery
from googleapiclient import errors
import json

# Store your full project ID in a variable in the format the API needs.
projectID = 'projects/{}'.format('whewatt-sandbox')

# Get application default credentials (possible only if the gcloud tool is
#  configured on your machine).
#gcloud auth application-default login
credentials = GoogleCredentials.get_application_default()

# Build a representation of the Cloud ML API.
ml = discovery.build('ml', 'v1', credentials=credentials,
            discoveryServiceUrl='https://storage.googleapis.com/cloud-ml/discovery/ml_v1_discovery.json')

request_data = {'instances':
  [
      {"CRIM": 0.00632,"ZN": 18.0,"INDUS": 2.31,"NOX": 0.538, "RM": 6.575, "AGE": 65.2, "DIS": 4.0900, "TAX": 296.0, "PTRATIO": 15.3},
      {"CRIM": 0.00332,"ZN": 0.0,"INDUS": 2.31,"NOX": 0.437, "RM": 7.7, "AGE": 40.0, "DIS": 5.0900, "TAX": 250.0, "PTRATIO": 17.3}
  ]
}

parent = '%s/models/%s/versions/%s' % (projectID, 'housing_prices', 'v1')
response = ml.projects().predict(body=request_data, name=parent).execute()
print "response={0}".format(response)