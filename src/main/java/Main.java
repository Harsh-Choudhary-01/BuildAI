import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import static spark.Spark.*;

import com.auth0.Auth0User;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.internal.org.apache.commons.lang3.exception.ExceptionContext;
import com.auth0.jwt.internal.org.bouncycastle.crypto.tls.ConnectionEnd;
import freemarker.ext.beans.HashAdapter;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;
import static spark.Spark.post;
import com.heroku.sdk.jdbc.DatabaseUrl;
public class Main
{
    static String clientId = System.getenv("AUTH0_CLIENT_ID");
    static String clientDomain = System.getenv("AUTH0_DOMAIN");
    private static SecureRandom random = new SecureRandom();
  public static void main(String[] args)
  {
      System.out.println("Hi");
      port(Integer.valueOf(System.getenv("PORT")));
      staticFileLocation("/spark/template/freemarker");
    get("/", (request, response) ->
    {
        Map<String, Object> attributes = new HashMap<>();
        Map<String , Object> user = new HashMap<>();
        user = getUser(request);
        attributes.put("user" , user.get("claims"));
        attributes.put("loggedIn" , user.get("loggedIn"));
        attributes.put("clientId" , clientId);
        attributes.put("clientDomain" , clientDomain);
        return new ModelAndView(attributes, "index.ftl");
    }, new FreeMarkerEngine());

    get("/build/:projectID" , (request, response) -> {
        return getProjectPage(request , response);
    }, new FreeMarkerEngine());

    get("/build" , (request , response) ->
    {
        return processBuildPage(request , response);
    }, new FreeMarkerEngine());

    get("/generic", (request, response) ->
    {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "generic.ftl");
    }, new FreeMarkerEngine());

    get("/elements", (request, response) ->
    {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "elements.ftl");
    }, new FreeMarkerEngine());

    post("/build", (request, response) ->
    {
        return processBuildPage(request , response);

    } , new FreeMarkerEngine());

  }
  static ModelAndView processBuildPage(Request request ,  Response response)
  {
      System.out.println("Processed Build Page");
      Connection connection = null;
      String newProjectName = request.queryParams("project-name"); //TODO: handle same project names
      String newProjectDesc = request.queryParams("project-description");
      Map<String, Object> attributes = new HashMap<>();
      Map<String , Object> user;
      String[] projects = new String[1];
      ArrayList<String> projectNames = new ArrayList<>();
      user = getUser(request);
      if((Boolean) user.get("loggedIn"))
      {
          attributes.put("loggedIn" , true);
          attributes.put("user" , user.get("claims"));
          try
          {
              connection = DatabaseUrl.extract().getConnection();
              Statement stmt = connection.createStatement();
              Map<String, Object> userInfo = (Map<String , Object>)user.get("claims");
              if(newProjectName != null)
              {
                  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (userID text , userProjects text[] , CONSTRAINT user_list UNIQUE(userID))");
                  System.out.println("1 ex done");
                  String newID = newID();
                  stmt.executeUpdate("INSERT INTO users(userID , userProjects) VALUES ('" + userInfo.get("user_id") + "' , '{" + newID + "}') ON CONFLICT(userID) DO UPDATE SET userProjects[array_length(users.userProjects, 1) + 1] = '" + newID + "'");
                  System.out.println("2 ex done");
                  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS projects (projectID text , description text , projectName text)");
                  System.out.println("3 ex done");
                  stmt.executeUpdate("INSERT INTO projects (projectID , description , projectName) VALUES ('" + newID + "' , '" + newProjectDesc + "' , '" + newProjectName + "')");
                  System.out.println("4 ex done");
              }
              ResultSet rs = stmt.executeQuery("SELECT userProjects FROM users WHERE userID = '" + userInfo.get("user_id") + "'");
              while(rs.next())
              {
                  Array project = rs.getArray("userProjects");
                  projects = (String[]) project.getArray() ;
              }
              System.out.println("Project array before for" + projects.toString());
              for(int i = 0; i < projects.length ; i ++)
              {
                  rs = stmt.executeQuery("SELECT projectName from projects WHERE projectID = '" + projects[i] + "'");
                  while (rs.next())
                  {
                      projectNames.add(rs.getString("projectName"));
                      System.out.println("Added project name for id: " + projects[i]);
                  }
              }

          }
          catch (Exception e)
          {
              System.out.println("Exception: " + e.toString());
          }
          finally {
              if(connection != null) try {connection.close();} catch (SQLException e) {}
          }
      }
      else
      {
          attributes.put("loggedIn" , false);
      }
      if(projects != null) {
          System.out.println("Project ID Array:  " + projects.toString());
          System.out.println("Project NAme array: " + projectNames.toString());
          System.out.println("Project Debug length: " + projects.length);
      }
      attributes.put("clientId" , clientId);
      attributes.put("clientDomain" , clientDomain);
      attributes.put("projectHashes" , projects);
      attributes.put("projects" , projectNames);
      return new ModelAndView(attributes , "build.ftl");
  }

  static ModelAndView getProjectPage(Request request , Response response)
  {
      String projectID = request.params(":projectID");
      Map<String , Object> user = getUser(request);
      Map<String, Object> attributes = new HashMap<>();
      attributes.put("loggedIn" , user.get("loggedIn"));
      attributes.put("clientId" , clientId);
      attributes.put("clientDomain" , clientDomain);
      if((Boolean) user.get("loggedIn"))
        attributes.put("user" , user.get("claims"));
      return new ModelAndView(attributes , "project.ftl");

  }

  static String newID()
  {
      return new BigInteger(130 , random).toString(32);
  }

  static Map<String , Object> getUser(Request request) //Returned object always contains a loggedIn key value and if that is true also contains a value with key claims of user info
  {
      Map<String , Object> user = new HashMap<>();
      Map<String , Object> userInfo;
      String token = request.queryParams("token");
      if(token == null) {
          if(request.session().attribute("token") == null)
          {
              user.put("loggedIn", false);
              System.out.println("auth is null");
          }
          else
          {
              userInfo = checkToken(request.session().attribute("token"));
              if(userInfo.containsKey("loggedIn"))
                  user = userInfo;
              else
                  user.put("loggedIn", false);
          }
      }
      else {
          userInfo = checkToken(token);
          if(userInfo.containsKey("loggedIn")) {
              request.session().attribute("token" , token);
              user = userInfo;
          }
          else
              user.put("loggedIn", false);
      }
      return user;
  }

  static Map<String , Object> checkToken(String token)
  {
      Map<String , Object> values = new HashMap<>();
      final String secret = System.getenv("AUTH0_CLIENT_SECRET");
      final byte[] decodedSecret = Base64.getUrlDecoder().decode(secret);
      try
      {
          final JWTVerifier verifier = new JWTVerifier(decodedSecret);
          final Map<String , Object> claims = verifier.verify(token);
          values.put("loggedIn" , true);
          values.put("claims" , claims);
          return  values;
      }
      catch (Exception e)
      {
          System.out.println("Invalid token");
          return values;
      }
  }
}
