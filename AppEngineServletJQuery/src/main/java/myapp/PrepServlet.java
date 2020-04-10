package myapp;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.IdentifiableValue;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.StatusCode;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;

//
import java.lang.reflect.Method;
import java.util.Collections;
//import javax.cache.Cache;
//import javax.cache.CacheException;
//import javax.cache.CacheFactory;
//import javax.cache.CacheManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PrepServlet extends HttpServlet {
  static final int PAGE_SIZE = 15;
  private final DatastoreService datastore;
  private final MemcacheService cache;

  public PrepServlet() {
    datastore = DatastoreServiceFactory.getDatastoreService();
    cache = MemcacheServiceFactory.getMemcacheService();

/*
    //Loop through all products and cache them
    Query q = new Query("Product");
    PreparedQuery pq = datastore.prepare(q);
    for (Entity result : pq.asIterable()) {
      cache.put(result.getProperty("Name"), result.getKey());
    }
    */

  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse response)
      throws IOException {

        String searchTerm = req.getParameter("term");
        System.out.println("Searchterm is " + searchTerm);

        if (searchTerm.equals("init")) {
            System.out.println("Product load start\n");

            String numOfEntries = req.getParameter("num");
            int num = Integer.parseInt(numOfEntries);
            for (int i=0; i < num; i++) {
              Entity product = new Entity("Product", Integer.toString(i));
              product.setProperty("Name", Integer.toString(i) + " Widget");
              datastore.put(product);
            }

            System.out.println("Product load complete\n");
            return;
        }

        if (searchTerm.equals("load")){
          loadProductNames();
          return;
        }

        Query q = new Query("Product");

        PreparedQuery pq = datastore.prepare(q);

        //Output all matching products in JSON
        try{
          response.setContentType("application/json");
          PrintWriter out = response.getWriter();
          String output = "{";
          boolean firstEntry = true;
          int i = 0;
          for (Entity result : pq.asIterable()) {
            i++;
            if (firstEntry) {
              output += "\"" + i +"\": \"" + result.getProperty("Name") + "\"";
              firstEntry = false;
            }
            else {
              output += ",";
              output += "\"" + i +"\": \"" + result.getProperty("Name") + "\"";
            }
          }
          output += "\", Number of items\": " +"\"" + i  + "\"";
          output += ("}");

          System.out.println(output);
          out.println(output);
          out.close();
        } catch (IOException e){
          e.printStackTrace();
        }

  }

  public void doPost(HttpServletRequest req, HttpServletResponse response)
      throws IOException {
    System.out.println(req.getParameter("product"));
  }

  private void loadProductNames(){
    System.out.println("Product load start\n");
    int productKey = 0;
    Document[] documents = new Document[10];

    documents[0] = createEntry(productKey++, "the big short");
    documents[1] = createEntry(productKey++, "the secret life of pets");
    documents[2] = createEntry(productKey++, "the magnolia story");
    documents[3] = createEntry(productKey++, "the walking dead");
    documents[4] = createEntry(productKey++, "the whistler");
    documents[5] = createEntry(productKey++, "the weekend");
    documents[6] = createEntry(productKey++, "the woman in cabin 10");
    documents[7] = createEntry(productKey++, "the witcher");
    documents[8] = createEntry(productKey++, "the wizard of oz");
    documents[9] = createEntry(productKey++, "the wild robot");

    IndexSpec indexSpec = IndexSpec.newBuilder().setName("Products").build();
    Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

     final int maxRetry = 3;
     int attempts = 0;
     int delay = 2;
     while (true) {
       try {
         index.put(documents);
       } catch (PutException e) {
         try{
           if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())
               && ++attempts < maxRetry) { // retrying
             Thread.sleep(delay * 1000);
             delay *= 2; // easy exponential backoff
             continue;
           } else {
             e.printStackTrace(); // otherwise throw
           }
         }  catch (Exception w) {
           w.printStackTrace();
         }
       }
       break;
     }
    System.out.println("Product load complete\n");

  }

  private Document createEntry(int key, String name){

    Entity product = new Entity("Product", Integer.toString(key));
    product.setProperty("Name", name);
    datastore.put(product);

     Document doc = Document.newBuilder()
         .setId(Integer.toString(key))
         .addField(Field.newBuilder().setName("name").setText(name))
         .build();

    return doc;
  }


}
