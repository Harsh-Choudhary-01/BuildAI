import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import com.auth0.NonceUtils;

import com.heroku.sdk.jdbc.DatabaseUrl;

public class Main
{
  public static void main(String[] args)
  {
    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/spark/template/freemarker");

    get("/", (request, response) ->
    {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");
            String clientId = System.getenv("AUTH0_CLIENT_ID");
            String clientDomain = System.getenv("AUTH0_DOMAIN");
            //NonceUtils.addNonceToStorage(request);
            attributes.put("clientId" , clientId);
            attributes.put("clientDomain" , clientDomain);
            return new ModelAndView(attributes, "index.ftl");
    }, new FreeMarkerEngine());

    get("/build" , (request , response) ->
    {
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
