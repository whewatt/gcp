// Configure the Google Cloud provider
provider "google" {
  project = "jinked-home"
  region  = "us-central1"
}

// Setup the Terraform state provider
terraform {
  backend "gcs" {
    bucket = "jinked-terraform-state"
    prefix = "jinked-home"
  }
}

variable "stats-zones" {
  default = ["us-central1-a", "us-central1-f"]
}
