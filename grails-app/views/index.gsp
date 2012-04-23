<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome to the Osler Message Broker Console</title>
		<g:set var="ds" value="${grailsApplication.config.dataSource.url.toString()}"/>
		<style type="text/css" media="screen">
			#status {
				background-color: #eee;
				border: .2em solid #fff;
				margin: 2em 0em 1em 2em;
				padding: 1em;
				width: 14em;
				float: left;
				-moz-box-shadow: 0px 0px 1.25em #ccc;
				-webkit-box-shadow: 0px 0px 1.25em #ccc;
				box-shadow: 0px 0px 1.25em #ccc;
				-moz-border-radius: 0.6em;
				-webkit-border-radius: 0.6em;
				border-radius: 0.6em;

			}

			.ie6 #status {
				display: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
			}

			#status ul {
				font-size: 0.9em;
				list-style-type: none;
				margin-bottom: 0.6em;
				padding: 0;
			}
            
			#status li {
				line-height: 1.3;
			}

			#status h1 {
				text-transform: uppercase;
				font-size: 1.1em;
				margin: 0 0 0.3em;
			}

			#page-body {
				margin: 2em 1em 1.25em 20em;
			}

			h2 {
				margin-top: 1em;
				margin-bottom: 0.3em;
				font-size: 1em;
			}

			p {
				line-height: 1.5;
				margin: 0.25em 0;
			}

			@media screen and (max-width: 480px) {
				#status {
					display: none;
				}

				#page-body {
					margin: 0 1em 1em;
				}

				#page-body h1 {
					margin-top: 0;
				}
			}
			
			#options {
				list-style: none;				
			}
			
			#options li {
				padding: 0 0 1em 3em;
			}
			
			#test-li {
				background: url(images/test-runner.png) no-repeat left top;
			}
			
			#log-li {
				background: url(images/analytics.png) no-repeat left top;
			}
			
			#routing-li {
				background: url(images/router.png) no-repeat left top;
			}
		</style>
	</head>
	<body>
		<a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="status" role="complementary">
			<h1>Console Status</h1>
			<ul>
				<li>Console Version: ${grailsApplication.metadata['app.version'] }</li>
				<li>MB Host: ${grailsApplication.config.osler.mb.mbHost}</li>
				<li>Environment: ${grails.util.GrailsUtil.environment}</li>
				<li>DB Host: ${(ds.indexOf("mysql") >= 0) ? ds.substring(ds.indexOf("//")+2,ds.indexOf("/",ds.indexOf("//")+2)) : "Unknown"}</li>
				<li>DB Name: ${(ds.indexOf("mysql") >= 0) ?ds.substring(ds.indexOf("/",ds.indexOf("//")+2)+1, ds.indexOf("?")) : "Unknown" }</li>
				<li>DB Username: ${grailsApplication.config.dataSource.username }</li>				
				<li>Schematic: <a href="https://docs.google.com/drawings/d/1F_-LBLroqUFj0bD7KDeTxOrZZOn2vx6fE-7uNQt0Ke8/edit">On Google Docs</a>				
			</ul>
		</div>
		<div id="page-body" role="main">
			<h1>Welcome to the Osler Message Broker Console</h1>
			This console allows you to work with the Message Broker component of the uOttawa Osler project.
			To get started, choose from the following options.
			<ul id="options">
				<li id="test-li"><h2><g:link controller="tester" action="index">Inject Test Events</g:link></h2>Use this tool to inject test events from a script into Message Broker.</li>
				<li id="log-li"><h2><g:link controller="log" action="index">Log Analytics</g:link></h2>Use this tool to view the Message Broker dashboard and log to ensure that everything is working properly.</li>
				<li id="routing-li"><h2><g:link controller="event" action="routing">Routing Rules</g:link></h2>Use this tool to change the routing rules that Message Broker uses. Add new events and destinations, as well as change the paths between them.</li> 
			</ul>
		</div>
	</body>
</html>
