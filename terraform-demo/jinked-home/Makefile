apply:
	terraform apply

init:
	terraform init

elasticsearch:
	terraform apply -target=google_compute_firewall.allow-ssh -auto-approve
	terraform apply -target=google_compute_firewall.allow-internal -auto-approve
	terraform apply -target=google_compute_instance_group_manager.elasticsearch -auto-approve

stats:
	terraform apply -target=google_compute_region_autoscaler.stats -auto-approve
	terraform apply -target=google_compute_region_backend_service.stats -auto-approve
	
bigquery:
	terraform apply -target=google_bigquery_table.events -auto-approve

destroy:
	terraform destroy