#!/usr/bin/env python
#
import webapp2
import logging
import uuid
from google.cloud import bigquery

class MainHandler(webapp2.RequestHandler):
    def get(self):
        valueToInsert = self.request.get('value')    
        
        if not valueToInsert :    
            self.response.write('Value is null')
        else :
            print('>>> should not be at this line')
            bigquery_client = bigquery.Client()
            dataset = bigquery_client.dataset('DataflowDemo')
            table = dataset.table('Demo')
            json_data = '{ "fieldone" : "%s"}' % valueToInsert
            data = json.loads(json_data)

            # Reload the table to get the schema.
            table.reload()

            rows = [data]
            errors = table.insert_data(rows)

            if not errors:
                print('Loaded 1 row into {}:{}'.format(dataset_name, table_name))
            else:
                print('Errors:')
                pprint(errors)
                
            self.response.write('Value inerted is') % valueToInsert
                        
app = webapp2.WSGIApplication([
    ('/', MainHandler)
], debug=True)
