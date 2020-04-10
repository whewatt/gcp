# App Engine Standard Environment BigQuery Sample

This sample demonstrates how to deploy an application on Google App Engine that inserts values into BigQuery using the streaming API.

NOTE:  The Python directory contains an incomplete Python sample.  Specifically, when deployed to GAE, an erorr is generated trying to import the ctypes library.

See the [Google App Engine standard environment documentation][ae-docs] for more detailed instructions.

[ae-docs]: https://cloud.google.com/appengine/docs/java/

## Setup

Use either:

* `gcloud init`
* `gcloud beta auth application-default login`

## Maven
### Running locally {NOT TESTED}

    $ mvn appengine:run

### Deploying to App Engine

    $ mvn appengine:update



## Using the app

    https://{gae url}?value={something to insert}

## Gradle {NOT TESTED}
### Running locally

    $ gradle appengineRun

If you do not have gradle installed, you can run using `./gradlew appengineRun`.

### Deploying

    $ gradle appengineDeploy

If you do not have gradle installed, you can deploy using `./gradlew appengineDeploy`.

<!--
## Intelij Idea
Limitations - Appengine Standard support in the Intellij plugin is only available for the Ultimate Edition of Idea.

### Install and Set Up Cloud Tools for IntelliJ

To use Cloud Tools for IntelliJ, you must first install IntelliJ IDEA Ultimate edition.

Next, install the plugins from the IntelliJ IDEA Plugin Repository.

To install the plugins:

1. From inside IDEA, open File > Settings (on Mac OS X, open IntelliJ IDEA > Preferences).
1. In the left-hand pane, select Plugins.
1. Click Browse repositories.
1. In the dialog that opens, select Google Cloud Tools.
1. Click Install.
1. Click Close.
1. Click OK in the Settings dialog.
1. Click Restart (or you can click Postpone, but the plugins will not be available until you do restart IDEA.)

### Running locally

### Deploying
 -->
