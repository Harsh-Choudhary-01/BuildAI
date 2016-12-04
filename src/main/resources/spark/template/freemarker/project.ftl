<!DOCTYPE html>
<html>
	<head>
		<title>BuildAI - Project Dashboard</title>
	    <meta charset="utf-8" />
	    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
	    <link rel="stylesheet" type="text/css" href="../css/main.css" />
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
	    <section id="main">
	    	<div class="inner">
	    		<#if loggedIn>
	    			<h1 style="display: inline"><a href="#">Project Name</a></h1>
	    			<nav id="nav" style="float: right">
	    				<a href="#">Data</a>
	    				<a href="#">Build</a>
	    				<a href="#">Raesults</a>
	    			</nav>
	    		<#else>
	    			<div class="row">
		    			<div class="12u 12u$(xsmall)" style="text-align: center">
			    			<ul class="actions vertical">
		                		<li><a href="#" class="button special signup">Please Log In</a></li>
		            		</ul>
	            		</div>
            		</div>
	    		</#if>
	    	</div>
	    </section>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js"></script>
	    <script src="../js/project.js"></script>
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