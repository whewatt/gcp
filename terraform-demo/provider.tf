provider "google" {
	project = "whewatt-sandbox"
	region 	= "us-east1"
}

provider "aws" {
	access_key = "xxx"
	secret_key = "xxx"
	region 	   = "us-east-1"
}

provider "azure" {
	subscription_id = "xxx"
	client_id	= "xxx"
	client_secret	= "xxx"
	tenant_id	= "xxx"
}
