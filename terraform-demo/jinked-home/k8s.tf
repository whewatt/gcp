// Create a k8s cluster
resource "google_container_cluster" "pandora" {
  name               = "pandora"
  zone               = "us-central1-a"
  network = "${google_compute_network.doofault.name}"
  subnetwork = "${google_compute_subnetwork.doofault-us-central1.name}"
  min_master_version = "1.9.6-gke.1"

  master_auth {
    username = "pandora"
    password = "amagicalclusterforpandora"
  }

  // Every node pool will replicate the number of nodes
  // out to additional zones, i.e. 3 nodes in the node pool * 2 = 9 nodes
  additional_zones = [
    "us-central1-b",
    "us-central1-c",
  ]

  // Main pool for the cluster
  node_pool {
    name = "default"
    node_count = 48

    management = {
      auto_repair = true
      auto_upgrade = true
    }
    
    autoscaling = {
      min_node_count = 144
      max_node_count = 180
    }

    node_config {
      oauth_scopes = [
        "https://www.googleapis.com/auth/cloud-platform",
      ]

      disk_size_gb = 30
      machine_type = "n1-standard-8"
    }
  }
}
