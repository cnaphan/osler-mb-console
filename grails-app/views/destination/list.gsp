
<%@ page import="osler.mb.routing.Destination" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'destination.label', default: 'Destination')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-destination" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" controller="event" action="routing"><g:message code="osler.mb.routing.EventRouting.title" /></g:link></li>				
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="body">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<div>
				Destinations represent end-points for events. Message Broker will route events to destinations based on Routing Rules. 
				Destinations have parameters that help Message Broker determine the best manner in which to send events. The typical
				mechanism is SOAP, according to the WSDL defined by BPM and PFM. New destinations can be added dynamically but not new 
				access methods. New access methods require modificaitons to the deployed Message Broker archive (BAR).
			</div>
			<g:messages/>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'osler.mb.routing.Destination.name.label')}" />
						<g:sortableColumn property="url" title="${message(code: 'osler.mb.routing.Destination.url.label')}" />
						<g:sortableColumn property="accessMethod" title="${message(code: 'osler.mb.routing.Destination.accessMethod.label')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${destinationInstanceList}" status="i" var="d">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">					
						<td><g:link action="show" id="${d.name}">${fieldValue(bean: d, field: "name")}</g:link></td>
						<td>${fieldValue(bean: d, field: "url")}</td>
						<td>${fieldValue(bean:d, field: "accessMethod") }</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${destinationInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
