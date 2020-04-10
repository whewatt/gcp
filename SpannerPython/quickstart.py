#!/usr/bin/env python

from google.cloud import spanner

def query_data(instance_id, database_id):
    """Queries sample data from the database using SQL."""
    spanner_client = spanner.Client()
    instance = spanner_client.instance(instance_id)
    database = instance.database(database_id)

    with database.snapshot() as snapshot:
        results = snapshot.execute_sql(
            'SELECT * FROM mezzanine_db')

        for row in results:
            print(row)

if __name__ == '__main__':
    query_data('wesinstance1', 'mezzanine_db')
