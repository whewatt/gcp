/**
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appengine.helloworld;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.DatasetInfo;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.InsertAllRequest;
import com.google.cloud.bigquery.InsertAllResponse;
import com.google.cloud.bigquery.QueryRequest;
import com.google.cloud.bigquery.QueryResponse;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardTableDefinition;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// [START example]
@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      String valueToInsert = req.getParameter("value");
	
  	if (valueToInsert == null)
  		return;
	
    System.out.println("valueToInsert is " + valueToInsert);
	
  	insertAll(valueToInsert);
	  
    PrintWriter out = resp.getWriter();
    out.println("Hello, world - Standard Servlet");
  }
  
  public void insertAll(String valueToInsert) {
      // [START insertAll]
      BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
	  TableId tableId = TableId.of("DataflowDemo", "Demo");
	  
	  Map<String, Object> firstRow = new HashMap<>();
      firstRow.put("fieldone", valueToInsert);
      
	  // Create an insert request
      InsertAllRequest insertRequest =
	          InsertAllRequest.newBuilder(tableId).addRow(firstRow).build();
      
	  // Insert rows
	  // insertAll is a Streaming API call
      InsertAllResponse insertResponse = bigquery.insertAll(insertRequest);
      // Check if errors occurred
      if (insertResponse.hasErrors()) {
        System.out.println("Errors occurred while inserting rows");
      }
    }
}
// [END example]
