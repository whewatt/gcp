resource "google_compute_firewall" "allow-ssh" {
  name    = "doofault-allow-ssh"
  network = "${google_compute_network.doofault.name}"

  allow {
    protocol = "tcp"
    ports = ["22"]
  }

  priority = 1000
}

resource "google_compute_firewall" "allow-internal" {
  name    = "doofault-allow-internal"
  network = "${google_compute_network.doofault.name}"

  allow {
    protocol = "tcp"
    ports = ["0-65535"]
  }

  allow {
    protocol = "udp"
    ports = ["0-65535"]
  }

  source_ranges = [
   "${google_compute_subnetwork.doofault-us-central1.ip_cidr_range}" 
  ]
  priority = 1000

}
