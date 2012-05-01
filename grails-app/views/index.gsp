<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome to the Osler Message Broker Console</title>
		<g:set var="ds" value="${grailsApplication.config.dataSource.url.toString()}"/>
		<style type="text/css">
			#options {
				list-style: none;				
				padding: 0.2em 0 0 2em;				
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
				<li>Transmission Method: ${grailsApplication.config.osler.mb.registerEventMethod }</li>
				<li>DB Host: ${(ds.indexOf("mysql") >= 0) ? ds.substring(ds.indexOf("//")+2,ds.indexOf("/",ds.indexOf("//")+2)) : "Unknown"}</li>
				<li>DB Name: ${(ds.indexOf("mysql") >= 0) ?ds.substring(ds.indexOf("/",ds.indexOf("//")+2)+1, ds.indexOf("?")) : "Unknown" }</li>
				<li>DB Username: ${grailsApplication.config.dataSource.username }</li>				
				<li>Schematic: <a href="https://docs.google.com/drawings/d/1F_-LBLroqUFj0bD7KDeTxOrZZOn2vx6fE-7uNQt0Ke8/edit">On Google Docs</a>				
			</ul>
		</div>
		<div id="body" class="indented">
			<h1>Welcome to the Osler Message Broker Console</h1>
			<g:messages/>
			This console allows you to work with the Message Broker component of the uOttawa Osler project.
			To get started, choose from the following options.
			<ul id="options">
				<li id="test-li">
					<h2><g:link controller="tester" action="index">Inject Test Events</g:link></h2>Use this tool to inject test events from a script into Message Broker.
					<div>
						<g:link controller="tester" action="index">Run Script</g:link> |
						<g:link controller="tester" action="locationTest"><g:message code="osler.mb.tester.locationTest.label"/></g:link>
					</div>
				</li>
				<li id="log-li">
					<h2><g:link controller="log" action="index">Log Analytics</g:link></h2>Use this tool to view the Message Broker dashboard and log to ensure that everything is working properly.
					<div>
						<g:link controller="log" action="index" params="${[viewfor:1]}">Last Hour</g:link> |
						<g:link controller="log" action="index" params="${[viewfor:2]}">Today</g:link> |
						<g:link controller="log" action="list">List</g:link>
						
					</div>
				</li>
				
				<li id="routing-li">
					<h2><g:link controller="event" action="routing">Routing Rules</g:link></h2>Use this tool to change the routing rules that Message Broker uses. Add new events and destinations, as well as change the paths between them.
					<div>
						<g:link controller="event" action="routing">Rules</g:link> |
						<g:link controller="event" action="list">Events</g:link> |
						<g:link controller="destination" action="list">Destinations</g:link> | 
						<g:link controller="source" action="list">Sources</g:link> 
					</div>
					</li> 
			</ul>
		</div>
	</body>
</html>
