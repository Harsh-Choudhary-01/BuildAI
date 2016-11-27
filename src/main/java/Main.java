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
        String token = request.queryParams("token");
        if(token == null) {
            if(request.session().attribute("token") == null)
            {
                attributes.put("loggedIn", false);
                System.out.println("auth is null");
            }
            else
            {
                user = checkToken(request.session().attribute("token"));
                if(user.containsKey("loggedIn")) {
                    attributes.put("user", user.get("claims"));
                    attributes.put("loggedIn", true);
                }
                else
                    attributes.put("loggedIn", false);
            }
        }
        else {
            if(request.session().attribute("token") == null)
                request.session().attribute("token" , token);
            user = checkToken(token);
            if(user.containsKey("loggedIn")) {
                attributes.put("user", user.get("claims"));
                attributes.put("loggedIn", true);
            }
            else
                attributes.put("loggedIn", false);
        }
        attributes.put("clientId" , clientId);
        attributes.put("clientDomain" , clientDomain);
        return new ModelAndView(attributes, "index.ftl");
    }, new FreeMarkerEngine());

    before("/login" ,  (request, response) ->
    {
        final String jwt = request.queryParams("token");
        request.session().attribute("jwt" , jwt);
        final String secret = System.getenv("AUTH0_CLIENT_SECRET");
        final byte[] decodedSecret = Base64.getUrlDecoder().decode(secret);
        try
        {
            final JWTVerifier verifier = new JWTVerifier(decodedSecret);
            final Map<String , Object> claims = verifier.verify(jwt);
            request.session().attribute("user" , claims);
        }
        catch (Exception e)
        {
            System.out.println("Invalid token");
        }
        if(request.queryParams("url") != null)
            response.redirect("/" + request.queryParams("url"));
        else
            response.redirect("/build");
    });
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
      Connection connection = null;
      String newProjectName = request.queryParams("project-name"); //TODO: handle same project names
      String newProjectDesc = request.queryParams("project-description");
      //stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (userID text, projects text[])");
      Map<String, Object> attributes = new HashMap<>();
      Map<String , Object> user;
      String token = request.queryParams("token");
      ArrayList<String> projects = new ArrayList<>();
      ArrayList<String> projectNames = new ArrayList<>();
      ArrayList<String> projectHashes;
      if(token == null) {
          if(request.session().attribute("token") == null)
          {
              attributes.put("loggedIn", false);
              System.out.println("auth is null");
          }
          else
          {
              user = checkToken(request.session().attribute("token"));
              if(user.containsKey("loggedIn")) {
                  attributes.put("user", user.get("claims"));
                  attributes.put("loggedIn", true);
              }
              else
                  attributes.put("loggedIn", false);
          }
      }
      else {
          user = checkToken(token);
          if(user.containsKey("loggedIn")) {
              request.session().attribute("token" , token);
              attributes.put("user", user.get("claims"));
              attributes.put("loggedIn", true);
          }
          else
              attributes.put("loggedIn", false);
      }
      if((Boolean) attributes.get("loggedIn"))
      {
          try
          {
              connection = DatabaseUrl.extract().getConnection();
              Statement stmt = connection.createStatement();
              Map<String, Object> userInfo = (Map<String , Object>)attributes.get("user");
              if(newProjectName != null)
              {
                  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (userID text , projects text[])");
                  System.out.println("1 ex done");
                  String newID = newID();
                  stmt.executeUpdate("INSERT INTO users u(u.userID , u.projects) VALUES ('" + userInfo.get("user_id") + "' , '{" + newID + "}') ON CONFLICT(userID) DO UPDATE SET u.projects[array_length(u.projects, 1) + 1] = '" + newID + "'");
                  System.out.println("2 ex done");
                  stmt.executeUpdate("CREATE TABLE IF NOT EXISTS projects (projectID text , description text , projectName text)");
                  System.out.println("3 ex done");
                  stmt.executeUpdate("INSERT INTO projects (projectID , description , projectName) VALUES ('" + newID + "' , '" + newProjectDesc + "' , '" + newProjectName + "')");
                  System.out.println("4 ex done");
                  stmt.executeUpdate("UPDATE users SET projects[array_length(projects, 1) + 1] = '" + newID +
                          "' WHERE userID = '" + userInfo.get("user_id") + "'");
              }
              ResultSet rs = stmt.executeQuery("SELECT projects FROM users WHERE userID = '" + userInfo.get("user_id") + "'");
              while(rs.next())
              {
                  projects = (ArrayList<String>) rs.getArray("projects").getArray();
              }
              for(int i = 0; i < projects.size() ; i ++)
              {
                  rs = stmt.executeQuery("SELECT projectName from projects WHERE projectID = '" + projects.get(i) + "'");
                  while (rs.next())
                  {
                      projectNames.add(rs.getString("projectName"));
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
      //for(int i = 0; i < projects.size(); i++)
      //{
      //    projectHashes.add(projects.get(i).replaceAll("\\s" , "").toLowerCase());
      //}
      projectHashes = projects;
      attributes.put("clientId" , clientId);
      attributes.put("clientDomain" , clientDomain);
      attributes.put("projectHashes" , projectHashes);
      attributes.put("projects" , projectNames);
      return new ModelAndView(attributes , "build.ftl");
  }
  static String newID()
  {
      return new BigInteger(130 , random).toString(32);
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
