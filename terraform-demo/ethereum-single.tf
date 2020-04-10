resource "google_compute_instance" "ethereum-single" {
    name    = "ethereum-single-node"
    machine_type = "custom-1-6144"
    zone = "us-central1-a"

    metadata_startup_script = "sudo add-apt-repository -y ppa:ethereum/ethereum && sudo apt-get update && sudo apt-get -y install ethereum && geth version"

    network_interface {
        network = "default"
        access_config {

        }
    }

    boot_disk {
        initialize_params{
           image = "ubuntu-1604-xenial-v20180612" 
           size = "50"
        }
    }
}