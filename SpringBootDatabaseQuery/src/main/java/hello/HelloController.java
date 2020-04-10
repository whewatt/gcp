package hello;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String index() {

      Connection Conn = null;
    	Statement Stmt = null;
    	ResultSet Resultset = null;

      String DBAAS_DEFAULT_CONNECT_DESCRIPTOR = System.getenv("DBAAS_DEFAULT_CONNECT_DESCRIPTOR");
  		String DBAAS_USER_NAME = System.getenv("DBAAS_USER_NAME");
  		String DBAAS_USER_PASSWORD = System.getenv("DBAAS_USER_PASSWORD");

      String output = " ";

      try {
  			Class.forName("oracle.jdbc.driver.OracleDriver");
  		} catch (ClassNotFoundException exception) {
  			return "Oracle Driver Class Not found Exception: " + exception.toString();
  		}

      try {
  			// Attempts to establish a connection
  			// here DB name: localhost, sid: crunchify
  			Conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DBAAS_DEFAULT_CONNECT_DESCRIPTOR,
                                              DBAAS_USER_NAME,
                                              DBAAS_USER_PASSWORD);
        Stmt = Conn.createStatement();

        Resultset = Stmt.executeQuery("SELECT * from XDEMO");

        while (Resultset.next()) {
    			output = output + Resultset.getString(1) + "  " + Resultset.getString(2) + " ";
    		}

        Resultset.close();
        Stmt.close();
        Conn.close();

        return "Table Details: " + output;

    }
    catch (SQLException e) {
      return "SQL Exception caught";
    }
  }
}
