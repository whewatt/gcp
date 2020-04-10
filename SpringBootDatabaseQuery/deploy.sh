#!/bin/sh
#Passed in as Build Parameters
export ID_DOMAIN=usatandttrial77
export USER_ID=gj3485@att.com
export USER_PASSWORD=$big7SID
export APP_NAME=SpringbootWebsim
export DEPLOY_FILE=deployment.json
export ARCHIVE_FILE=springboot-helloworld-dist.zip
export ARCHIVE_LOCAL=target/$ARCHIVE_FILE
export APAAS_HOST=apaas.us.oraclecloud.com

# CREATE CONTAINER
echo '\n[info] Creating container\n'
curl -i -X PUT \
    -u ${USER_ID}:${USER_PASSWORD} \
    https://${ID_DOMAIN}.storage.oraclecloud.com/v1/Storage-$ID_DOMAIN/$APP_NAME

# PUT ARCHIVE IN STORAGE CONTAINER
echo '\n[info] Uploading application to storage\n'
curl -i -X PUT \
  -u ${USER_ID}:${USER_PASSWORD} \
  https://${ID_DOMAIN}.storage.oraclecloud.com/v1/Storage-$ID_DOMAIN/$APP_NAME/$ARCHIVE_FILE \
      -T $ARCHIVE_LOCAL

# See if application exists
let httpCode=`curl -i -X GET  \
  -u ${USER_ID}:${USER_PASSWORD} \
  -H "X-ID-TENANT-NAME:${ID_DOMAIN}" \
  -H "Content-Type: multipart/form-data" \
  -sL -w "%{http_code}" \
  https://${APAAS_HOST}/paas/service/apaas/api/v1.1/apps/${ID_DOMAIN}/${APP_NAME} \
  -o /dev/null`

# If application exists...
if [ $httpCode == 200 ]
then
  # Update application
  echo '\n[info] Updating application...\n'
  curl -i -X PUT  \
    -u ${USER_ID}:${USER_PASSWORD} \
    -H "X-ID-TENANT-NAME:${ID_DOMAIN}" \
    -H "Content-Type: multipart/form-data" \
    -F archiveURL=${APP_NAME}/${ARCHIVE_FILE} \
    -F "deployment=@${DEPLOY_FILE}" \
    https://${APAAS_HOST}/paas/service/apaas/api/v1.1/apps/${ID_DOMAIN}/${APP_NAME}
else
  # Create application and deploy
  echo '\n[info] Creating application...\n'
  curl -i -X POST  \
    -u ${USER_ID}:${USER_PASSWORD} \
    -H "X-ID-TENANT-NAME:${ID_DOMAIN}" \
    -H "Content-Type: multipart/form-data" \
    -F "name=${APP_NAME}" \
    -F "runtime=java" \
    -F "subscription=Hourly" \
    -F archiveURL=${APP_NAME}/${ARCHIVE_FILE} \
    -F "deployment=@${DEPLOY_FILE}" \
    https://${APAAS_HOST}/paas/service/apaas/api/v1.1/apps/${ID_DOMAIN}
fi
echo '\n[info] Deployment complete\n'
