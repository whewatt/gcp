#!/bin/bash
if [ "$1" = "" ]; then
  echo "String argument required"
else
export GCLOUD_PROJECT=whewatt-sandbox
export GOOGLE_APPLICATION_CREDENTIALS=../../whewatt-sandbox-60e751f0f5a5.json
pushd ../nodejs-docs-samples/dlp/
node inspect string $1
popd
fi
