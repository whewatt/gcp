# Terraform Notes

GCP Reference Documentation at https://www.terraform.io/docs/providers/google/index.html#

jinked-home - Examples of using Terraform with BigQuery, K8S, Elastic, etc.

provider.tf - specifies the cloud providers to download and initialize.

  terraform init

ethereum-single.tf - creates one VM.  Notice the startup scripts executed by the "metadata_startup_script" element

  terraform plan
  terraform apply

terraform.tfstate - the state of cloud resources as known to Terraform

Destroy all resources

  terraform destroy