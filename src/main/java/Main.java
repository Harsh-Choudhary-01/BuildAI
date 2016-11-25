import java.util.Base64;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import static spark.Spark.*;

import com.auth0.Auth0User;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.internal.org.apache.commons.lang3.exception.ExceptionContext;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;
import com.heroku.sdk.jdbc.DatabaseUrl;
public class Main
{
  public static void main(String[] args)
  {
      System.out.println("Hi");
      port(Integer.valueOf(System.getenv("PORT")));
      staticFileLocation("/spark/template/freemarker");
      String clientId = System.getenv("AUTH0_CLIENT_ID");
      String clientDomain = System.getenv("AUTH0_DOMAIN");
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
        Map<String, Object> attributes = new HashMap<>();
        Map<String , Object> user = new HashMap<>();
        String token = request.queryParams("token");
        ArrayList<String> projects = new ArrayList<>();
        ArrayList<String> projectHashes = new ArrayList<>();
        projects.add("Project X");
        projects.add("Project Y");
        projects.add("Project Z");
        for(int i = 0; i < projects.size(); i++)
        {
            projectHashes.add(projects.get(i).replaceAll("\\s" , "").toLowerCase());
        }
        if(token == null) {
            if(request.session().attribute("token") == null)
            {
                attributes.put("loggedIn", false);
                System.out.println("auth is null");
            }
            else
            {
                if(request.session().attribute("token") == null)
                    request.session().attribute("token" , token);
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
                attributes.put("user", user.get("claims"));
                attributes.put("loggedIn", true);
            }
            else
                attributes.put("loggedIn", false);
        }
        attributes.put("clientId" , clientId);
        attributes.put("clientDomain" , clientDomain);
        attributes.put("projectHashes" , projectHashes);
        attributes.put("projects" , projects);
        return new ModelAndView(attributes , "build.ftl");
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
