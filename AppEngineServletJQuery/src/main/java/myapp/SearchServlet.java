package myapp;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.StatusCode;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;

//
import java.lang.reflect.Method;
import java.util.Collections;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet {
  private final IndexSpec indexSpec;
  private final Index index;

  public SearchServlet() {
    indexSpec = IndexSpec.newBuilder().setName("Products").build();
    index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse response)
      throws IOException {

        String searchTerm = req.getParameter("term");
        System.out.println("Searchterm is " + searchTerm);

        Results<ScoredDocument> results = testSearch(searchTerm);

        try {
          PrintWriter out = response.getWriter();
          String output = "{";
          boolean firstEntry = true;
          int i = 0;

          for (ScoredDocument doc : results) {
            i++;
            if (firstEntry) {
              output += "\"" + i +"\": \"" + doc.getOnlyField("name").getText() + "\"";
              firstEntry = false;
            }
            else {
              output += ",";
              output += "\"" + i +"\": \"" + doc.getOnlyField("name").getText() + "\"";
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

  private Results<ScoredDocument> testSearch(String searchTerm){

    SortOptions sortOptions = SortOptions.newBuilder()
          .addSortExpression(SortExpression.newBuilder()
              .setExpression("name")
              .setDirection(SortExpression.SortDirection.ASCENDING)
              .setDefaultValue(""))
          .setLimit(10)
          .build();

    QueryOptions options = QueryOptions.newBuilder()
        .setFieldsToReturn("name")
        .setSortOptions(sortOptions)
        .build();

    Query query = Query.newBuilder().setOptions(options).build(searchTerm);

    // search at least once
    Results<ScoredDocument> result = index.search(query);
    int numberRetrieved = result.getNumberReturned();

    return result;

  }

}
