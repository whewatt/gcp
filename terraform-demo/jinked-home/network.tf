resource "google_compute_network" "doofault" {
  name                    = "doofault"
  auto_create_subnetworks = "false"
}

resource "google_compute_subnetwork" "doofault-us-central1" {
  name                     = "doofault-us-central1"
  ip_cidr_range            = "10.0.0.0/16"
  network                  = "${google_compute_network.doofault.self_link}"
  region                   = "us-central1"
  private_ip_google_access = true
}
