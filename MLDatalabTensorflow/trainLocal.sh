gcloud ml-engine local train \
   --module-name=trainer.task \
   --package-path=trainer \
   -- \
   --output_dir='./output'
