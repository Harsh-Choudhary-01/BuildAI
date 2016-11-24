import java.security.Principal;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;

import com.auth0.NonceUtils;
import com.auth0.jwt.internal.org.apache.commons.lang3.SystemUtils;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import com.auth0.Auth0User;
import com.auth0.SessionUtils;

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
        NonceUtils.addNonceToStorage(request.raw());
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("message", "Hello World!");
        Auth0User user = SessionUtils.getAuth0User(request.raw()); //TODO: verify user with database
        if(user != null) {
            attributes.put("user", user);
            attributes.put("loggedIn" , true);
        }
        else
            attributes.put("loggedIn" , false);
        attributes.put("clientId" , clientId);
        attributes.put("clientDomain" , clientDomain);
        return new ModelAndView(attributes, "index.ftl");
    }, new FreeMarkerEngine());

    get("/build" , (request , response) ->
    {
        //NonceUtils.addNonceToStorage(request.raw());
        ArrayList<String> projects = new ArrayList<>();
        ArrayList<String> projectHashes = new ArrayList<>();
        projects.add("Project X");
        projects.add("Project Y");
        projects.add("Project Z");
        for(int i = 0; i < projects.size(); i++)
        {
            projectHashes.add(projects.get(i).replaceAll("\\s" , ""));
        }
        Map<String, Object> attributes = new HashMap<>();
        Auth0User user = (Auth0User) request.session().attribute("auth0User");
        Auth0User user1 = SessionUtils.getAuth0User(request.raw());
        Principal user2 = request.raw().getUserPrincipal();
        if(user == null)
            System.out.println("User Null");
        if(user1 == null)
            System.out.println("User 1 Null");
        if(user2 == null)
            System.out.println("User 2 Null");
        if(user != null) {
            attributes.put("userString" , user.toString());
            attributes.put("user", user);
            attributes.put("loggedIn" , true);
        }
        else
            attributes.put("loggedIn" , false);
        attributes.put("clientId" , clientId);
        attributes.put("clientDomain" , clientDomain);
        attributes.put("message", "Hello World!");
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
}
