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

public class DemoServlet extends HttpServlet {
  static final int PAGE_SIZE = 15;
  private final DatastoreService datastore;
  private final MemcacheService cache;

  public DemoServlet() {
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

    Query q =
      new Query("Product")
           .setFilter(
              CompositeFilterOperator.and(
                new FilterPredicate("Name", FilterOperator.GREATER_THAN, searchTerm),
                new FilterPredicate("Name", FilterOperator.LESS_THAN, searchTerm + "zz")))
            .addSort("Name", SortDirection.ASCENDING);

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

}
