<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!-->
<html lang="en" class="no-js">
<!--<![endif]-->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title><g:layoutTitle default="Survey Manager" /></title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"
	type="text/css">-->
<link rel="stylesheet" href="${resource(dir: 'css', file: 'sparse.css')}"
	type="text/css">	
<g:layoutHead />
<r:layoutResources />
</head>
<body>
	<div id="header">
		<a href="${ createLink(uri: '/') }" style="float: left;"><g:img file="mb-logo.png" alt="Osler Message Broker Console"/></a>
		<div id="home" style="float: left;">			
			<h1><a href="${createLink(uri: '/') }">Osler Message Broker Console</a></h1>
		</div>
		<div id="icons" style="float: right;">
			<g:link controller="tester" action="index"><g:img file="test-runner.png" alt="Inject Test Events" width="25" height="30"/></g:link>
			<g:link controller="log" action="index"><g:img file="analytics.png" alt="Log Analytics" width="31" height="31"/></g:link>
			<g:link controller="event" action="routing"><g:img file="router.png" alt="Update Routing Rules" width="30" height="30"/></g:link>
		</div>
		<div class="clearer" ></div>
	</div>	
	<g:layoutBody />
	<div id="spinner" class="spinner" style="display: none;">
		<g:message code="spinner.alt" default="Loading&hellip;" />
	</div>
	<g:javascript library="application" />
	<r:layoutResources />
</body>
</html>