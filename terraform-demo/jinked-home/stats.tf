resource "google_compute_instance_template" "stats" {
  name        = "stats"
  description = "Template for launching stats collector"

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
    disk_size_gb = "12"
  }

  metadata {
    "gce-container-declaration" = "${file("./conf/statsd.yml")}"
    "startup-script"            = "${file("./startup/setup.sh")}"
  }

  tags = ["stats-collector"]

  // Please don't do this.
  service_account {
    scopes = ["cloud-platform"]
  }
}

resource "google_compute_health_check" "stats" {
  name                = "stats"
  check_interval_sec  = 5
  timeout_sec         = 5
  healthy_threshold   = 2
  unhealthy_threshold = 10

  tcp_health_check {
    port = "8125"
  }
}

resource "google_compute_target_pool" "stats" {
  name = "stats"
}

resource "google_compute_region_instance_group_manager" "stats" {
  name = "stats"

  base_instance_name = "stats"
  instance_template  = "${google_compute_instance_template.stats.self_link}"
  update_strategy    = "NONE"
  region             = "us-central1"

  //target_pools       = ["${google_compute_target_pool.stats.self_link}"]

  named_port {
    name = "stats"
    port = 8125
  }
}

resource "google_compute_region_autoscaler" "stats" {
  name   = "stats"
  region = "us-central1"
  target = "${google_compute_region_instance_group_manager.stats.self_link}"

  autoscaling_policy = {
    max_replicas    = 92
    min_replicas    = 12
    cooldown_period = 60

    cpu_utilization {
      target = 0.5
    }
  }
}

/*
resource "google_compute_region_backend_service" "stats" {
  name        = "stats"
  description = "Region Backend for stats"

  //protocol         = "HTTP"
  timeout_sec      = 10
  session_affinity = "NONE"

  backend {
    group = "${google_compute_region_instance_group_manager.stats.instance_group}"
  }

  health_checks = ["${google_compute_health_check.stats.self_link}"]
}
*/

