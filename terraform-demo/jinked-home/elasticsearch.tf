resource "google_compute_instance_template" "elasticsearch" {
  name        = "elasticsearch"
  description = "Template for launching elasticsearch and image deploys as a cluster"

  machine_type = "n1-standard-8"

  network_interface {
    subnetwork    = "${google_compute_subnetwork.doofault-us-central1.name}"
    access_config = {}
  }

  scheduling {
    automatic_restart   = true
    on_host_maintenance = "MIGRATE"
  }

  disk {
    source_image = "cos-cloud/cos-stable"
    auto_delete  = false
    boot         = true
    disk_type    = "pd-standard"
    disk_size_gb = "128"
  }

  metadata {
    "gce-container-declaration" = "${file("./conf/elastic.yml")}"
    "startup-script"            = "${file("./startup/setup.sh")}"
  }

  tags = ["elasticsearch"]

  service_account {
    scopes = ["cloud-platform"]
  }
}

resource "google_compute_health_check" "elasticsearch" {
  name                = "elasticsearch"
  check_interval_sec  = 5
  timeout_sec         = 5
  healthy_threshold   = 2
  unhealthy_threshold = 10              # 50 seconds

  http_health_check {
    request_path = "/_cluster/health"
    port         = "9200"
  }
}

resource "google_compute_instance_group_manager" "elasticsearch" {
  name = "elasticsearch"

  base_instance_name = "elasticsearch"
  instance_template  = "${google_compute_instance_template.elasticsearch.self_link}"
  update_strategy    = "NONE"
  zone               = "us-central1-f"
  target_size        = 6

  named_port {
    name = "elasticsearch"
    port = 9200
  }
}
