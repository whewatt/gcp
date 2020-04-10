# Tool builder: `gcr.io/cloud-builders/mvn`

This Cloud Build builder runs Maven. It also includes a number of dependencies
that are precached within the image.

The output will be an image in Google Container Repository with the tag gcr.io/$PROJECT_ID/spring-boot:latest

## Building this builder

To build this builder, run the following command in this directory.

    $ gcloud builds submit . --config=cloudbuild.yaml

To deploy the container to App Engine, create a local app.yaml file

    runtime: custom
    env: flex
    service: spring-boot-cloudbuild 

Then
    gcloud app deploy --image-url=gcr.io/whewatt-sandbox/spring-boot:latest