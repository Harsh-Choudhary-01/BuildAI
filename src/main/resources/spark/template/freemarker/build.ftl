<!DOCTYPE HTML>

<html>
  <head>
    <title>BuildAI - Build Dashboard</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="css/main.css" />
    <script type="text/javascript">
      var uri = window.location.toString();
      if (uri.indexOf("?") > 0) {
        var clean_uri = uri.substring(0, uri.indexOf("?"));
        window.history.replaceState({}, document.title, clean_uri);
      }
    </script>
  </head>
  <body>

    <header id = "header">
      <div class="inner">
        <a href="/" class="logo link">BuildAI</a>
        <nav id="nav">
          <a href="/build" class="link">Build</a>
          <a href="/generic">Learn</a>
          <#if loggedIn>
            <a href="/profile" class="username link">${user.nickname}</a>
          <#else>
            <a href="#" class="signup username">Login</a>
          </#if>
        </nav>
      </div>
    </header>
    <a href="#menu" class="navPanelToggle"><span class="fa fa-bars"></span></a>

    <!--List of the projects  -->
    <section id="main">
      <div class="inner">
        <h1>Projects</h1>
        <section>
          <div class="row">
            <#if projects?size != 0>
              <div class="7u 12u$(xsmall)">
                <ul class="alt">
                  <#list projects as x>
                    <li><a href="#${projectHashes[x?counter - 1]}">${x}</a></li>
                  </#list>
                </ul>
              </div>
              <div class="-1u 3u 12u$(xsmall)" style="text-align: center">
                <ul class="actions vertical">
                  <li><a href="#new" class="button special">Create New Project</a></li>
                </ul>
              </div>
            </#if>
            <#if projects?size == 0>
              <div class="12u 12u$(xsmall)" style="text-align: center">
                <strong>No Projects?</strong>
                <ul class="actions vertical">
                  <li><a href="#new" class="button special">Create New Project</a></li>
                </ul>
              </div>
          </#if>
          </div>
        </section>
      </div>
    </section>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
    <script src="js/project.js"></script>
    <script src="https://cdn.auth0.com/js/lock/10.0/lock.min.js"></script>
    <script>
      $(function()
      {
        $('.link').each(function()
          {
            $(this).attr('href' , this.href + "?token=" + localStorage.getItem('id_token'));
          });
      });
      $(document).ready(function()
      {
        if(!${loggedIn?c}) {
          var lock = new Auth0Lock('${clientId}', '${clientDomain}', {
              auth: {
                params: {
                  scope: 'openid user_id name nickname email picture'
                }
              }
          });
          $('.signup').click(function(e) {
              e.preventDefault();
              lock.show();
          });
          lock.on("authenticated", function(authResult) {
              localStorage.setItem('id_token', authResult.idToken);
              window.location.href = "/build?token=" + authResult.idToken;
          });
        }
      });
    </script>
  </body>
</html>
