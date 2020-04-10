resource "google_bigquery_dataset" "umaddsbro" {
  dataset_id    = "umaddsbro"
  friendly_name = "umaddsbro"
  description   = "MADDS dataset ID"
  location      = "US"

  labels {
    env = "prod"
  }
}

resource "google_bigquery_table" "events" {
  dataset_id = "${google_bigquery_dataset.umaddsbro.dataset_id}"
  table_id   = "events"

  time_partitioning {
    type = "DAY"
  }

  labels {
    env = "prod"
  }

  schema = "${file("./schema/umaddsbro.events.json")}"
}
