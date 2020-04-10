# Take streaming data through Pub/Sub, into BigQuery, and query it with R Studio

## Setup
Use Cloud Launcher to create an R Studio Pro VM
Retain the login/pw info from the Deployment Manager

## Start R Studio
	gcloud config set project whewatt-demo
	gcloud compute instances start rstudio-server-pro-for-bigquery-1-vm --zone=us-central1-f

## Stream data into BigQuery using Dataflow
	gsutil ls gs://dataflow-samples/shakespeare/kinglear.txt

	cd /Users/whewatt/Projects/DataflowJavaSDK/examples
	vi src/main/java/com/google/cloud/dataflow/examples/complete/StreamingWordExtract.java

	mvn compile exec:java -Dexec.mainClass=com.google.cloud.dataflow.examples.complete.StreamingWordExtract -Dexec.args="--pubsubTopic=projects/whewatt-demo/topics/StreamingWordTopic --bigQueryDataset=DataflowDemo --stagingLocation=gs://whewatt-demo-dataflow"

	bq query "SELECT * from [whewatt-demo:DataflowDemo.streamingwordextract_whewatt_1127184111] LIMIT 10"

## Query from R
	Open R Studio web page

### Use menu or enter the following code to install bq library:  
	install.packages("bigrquery")

### Enter the following code in the browser
	project <- "whewatt-demo"
	sql <- "SELECT * from [whewatt-demo:DataflowDemo.streamingwordextract_whewatt_1127184111] LIMIT 10"
	query_exec(sql, project = project)

## Cleanup
	gcloud compute instances stop rstudio-server-pro-for-bigquery-1-vm --zone=us-central1-f
