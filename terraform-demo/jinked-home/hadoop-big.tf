// Setup a huge Dataproc cluster with large compute usage
resource "google_dataproc_cluster" "big-cluster" {
  name       = "big-cluster"
  region     = "us-central1"

  cluster_config {
    
    gce_cluster_config {
      subnetwork = "${google_compute_subnetwork.doofault-us-central1.name}"
      internal_ip_only = true
    }

    master_config {
      num_instances     = 3
      machine_type      = "n1-standard-4"
      disk_config {
          boot_disk_size_gb = 30
      }
    }

    worker_config {
      num_instances     = 600
      machine_type      = "n1-standard-4"
      disk_config {
        boot_disk_size_gb = 20
      }
    }

    preemptible_worker_config {
      num_instances     = 700
    }
  }
}