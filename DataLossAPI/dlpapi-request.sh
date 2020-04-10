#!/bin/bash
#See instructions at https://cloud.google.com/dlp/docs/quickstart-json to get a private key file
#May need to get a fresh access token
#  gcloud auth activate-service-account --key-file=JSON key file
#  gcloud auth print-access-token
curl -s -k -H "Authorization: Bearer ya29.El_UBHPmkQtG9bs4-5kH9j0VH_A75Ocqb1hrYDngOrzm99W51bmQKVd0NpOukjIDX4_aKYQCdXKPboSDy6fPwcsKWejQS9KO9YdvObUpVMTrKm7Cmcyp9OBhS7CY9OhDNQ"  -H "Content-Type: application/json"   https://dlp.googleapis.com/v2beta1/content:inspect -d @dlp-request.json

