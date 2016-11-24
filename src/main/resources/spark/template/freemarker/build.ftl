<!DOCTYPE HTML>

<html>
  <head>
    <title>BuildAI - Build Dashboard</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
    <link rel="stylesheet" type="text/css" href="css/main.css" />
  </head>
  <body>

    <header id = "header">
      <div class="inner">
        <a href="/" class="logo">BuildAI</a>
        <nav id="nav">
          <a href="/build">Build</a>
          <a href="/generic">Learn: ${loggedIn?c}</a>
          <#if loggedIn>
            <a href="/elements">${user.nickname}</a>
          <#else>
            <a href="#signup" class="signup">Login</a>
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
<!--     <script src="https://cdn.auth0.com/js/lock/10.0/lock.min.js"></script>
    <script>
      var lock = new Auth0Lock('${clientId}', '${clientDomain}', {
        auth: {
          redirectUrl: 'http://localhost:5000/build',
          responseType: 'code',
          params: {
                  scope: 'openid user_id name nickname email picture'
                }
            }
          });

      $(document).ready(function()
      {
        $('.signup').click(function()
        {
          doSignup();
        });
      });

      function doSignup() {
              lock.show();
        }
    </script> -->
  </body>
</html>
