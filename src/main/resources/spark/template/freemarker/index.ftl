<!DOCTYPE HTML>
<html>
<head>
	<title>BuildAI</title>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
	<link rel="stylesheet" type="text/css" href="css/main.css" />
</head>
<body>
	<!-- Header -->
	<header id="header">
		<div class="inner">
			<a href="/" class="logo">BuildAI</a>
			<nav id="nav">
				<a href="/build">Build</a>
				<a href="/generic">Learn</a>
				<#if !loggedIn>
					<a href="#signup" class="signup">Login</a>
				<#else>
					<a href="/elements">${user.nickname}</a>
				</#if>
			</nav>
		</div>
	</header>
	<a href="#menu" class="navPanelToggle"><span class="fa fa-bars"></span></a>
	<!-- Banner -->
	<section id="banner">
		<div class="inner">
			<h1>BuildAI: <span>An online machine<br />
				learning tool and teacher</span></h1>
				<ul class="actions">
					<li><a href="#signup" class="button alt signup">Get Started</a></li>
				</ul>
			</div>
		</section>

		<!-- One -->
		<section id="one">
			<div class="inner">
				<header>
					<h2>No Code Required</h2>
				</header>
				<p>Our online tool has built in features for data processing, reading, and storing. Simply choose or load your training data, set up an algorithim of choice, and use the trained program through a simple interface. All without any code.</p>
				<ul class="actions">
					<li><a href="#" class="button alt">Learn More</a></li>
				</ul>
			</div>
		</section>

		<!-- Two -->
		<section id="two">
			<div class="inner">
				<article>
					<div class="content">
						<header>
							<h3>Powerful Data Tools</h3>
						</header>
						<div class="image fit">
							<img src="images/pic01.jpg" alt="" />
						</div>
						<p>Our data tools include advanced reading capabilities for most files extensions including CSV and TXT. In addition we also offer tools for automatically fetching data from databases or even user defined websites. With these tools there is never a problem collecting and using data properly for your machine learning programs.</p>
					</div>
				</article>
				<article class="alt">
					<div class="content">
						<header>
							<h3>Machine Learning Course</h3>
						</header>
						<div class="image fit">
							<img src="images/pic02.jpg" alt="" />
						</div>
						<p>In addition to our online services for training machine learning programs we also offer an interactive course to get anyone up to speed on machine learning and how to utilitize it effectively. This course is ideal for people of all skill level with a variety of tracks to complete.</p>
					</div>
				</article>
			</div>
		</section>

		<!-- Three -->
		<section id="three">
			<div class="inner">
				<article>
					<div class="content">
						<span class="icon fa-laptop"></span>
						<header>
							<h3>Tempus Feugiat</h3>
						</header>
						<p>Morbi interdum mollis sapien. Sed ac risus. Phasellus lacinia, magna lorem ullamcorper laoreet, lectus arcu.</p>
						<ul class="actions">
							<li><a href="#" class="button alt">Learn More</a></li>
						</ul>
					</div>
				</article>
				<article>
					<div class="content">
						<span class="icon fa-diamond"></span>
						<header>
							<h3>Aliquam Nulla</h3>
						</header>
						<p>Ut convallis, sem sit amet interdum consectetuer, odio augue aliquam leo, nec dapibus tortor nibh sed.</p>
						<ul class="actions">
							<li><a href="#" class="button alt">Learn More</a></li>
						</ul>
					</div>
				</article>
				<article>
					<div class="content">
						<span class="icon fa-laptop"></span>
						<header>
							<h3>Sed Magna</h3>
						</header>
						<p>Suspendisse mauris. Fusce accumsan mollis eros. Pellentesque a diam sit amet mi ullamcorper vehicula.</p>
						<ul class="actions">
							<li><a href="#" class="button alt">Learn More</a></li>
						</ul>
					</div>
				</article>
			</div>
		</section>

		<!-- Footer -->
		<section id="footer">
			<div class="inner">
				<header>
					<h2>Get in Touch</h2>
				</header>
				<form method="post" action="#">
					<div class="field half first">
						<label for="name">Name</label>
						<input type="text" name="name" id="name" />
					</div>
					<div class="field half">
						<label for="email">Email</label>
						<input type="text" name="email" id="email" />
					</div>
					<div class="field">
						<label for="message">Message</label>
						<textarea name="message" id="message" rows="6"></textarea>
					</div>
					<ul class="actions">
						<li><input type="submit" value="Send Message" class="alt" /></li>
					</ul>
				</form>
				<div class="copyright">
					&copy; Untitled Design: <a href="https://templated.co/">TEMPLATED</a>. Images <a href="https://unsplash.com/">Unsplash</a>
				</div>
			</div>
		</section>

	<!-- Scripts -->
	<script src="js/jquery.min.js"></script>
	<script src="js/skel.min.js"></script>
	<script src="js/util.js"></script>
	<script src="js/main.js"></script>
	<script src="https://cdn.auth0.com/js/lock/10.0/lock.min.js"></script>
	<script>
		var lock = new Auth0Lock('${clientId}', '${clientDomain}', {
			auth: {
				redirectUrl: 'http://buildai.net/build',
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
	</script>
</body>
</html>
