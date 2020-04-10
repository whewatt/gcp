GCS_BUCKET=gs://whewatt-sandbox-ml/housing #CHANGE THIS TO YOUR GCS BUCKET
JOBNAME=housing_distributed_$(date -u +%y%m%d_%H%M%S)

gcloud ml-engine jobs submit training $JOBNAME \
   --region=us-east1 \
   --module-name=trainer.task \
   --package-path=./trainer \
   --job-dir=$GCS_BUCKET/$JOBNAME/ \
   --scale-tier=STANDARD_1 \  
   -- \
   --output_dir=$GCS_BUCKET/$JOBNAME/output
