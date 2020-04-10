#!/bin/bash
#May need to get a fresh access token
#  gcloud auth activate-service-account --key-file=JSON key file
#  gcloud auth print-access-token
#
curl -s -k -H "Content-Type: application/json" \
      -H "Authorization: Bearer ya29.El_oBEt5tOHqq3NZZmKoHbzynDYDqgWofqINPPeUyNISii8l7rtX_tYW2T2HByQi8T57GAPEztfKb6BaoAVFNMpu5v5LtsYoRAdozvUSOJwAbNnknxsBSM0mcgMBwYtwfQ" \
      https://speech.googleapis.com/v1beta1/speech:syncrecognize \
          -d @speechapi-request.json
