
<%@ page import="osler.mb.routing.Source" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'source.label', default: 'Source')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-source" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" controller="event" action="routing"><g:message code="osler.mb.routing.EventRouting.title" /></g:link></li>				
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-source" class="content tight-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<div><g:message code="osler.mb.routing.Source.advice"/></div>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="name" title="${message(code: 'osler.mb.routing.Source.name.label')}" />						
						<g:sortableColumn property="accessMethod" title="${message(code: 'osler.mb.routing.Source.accessMethod.label')}" />
						<g:sortableColumn property="matchingString" title="${message(code: 'osler.mb.routing.Source.matchingString.label')}" />
					</tr>
				</thead>
				<tbody>
				<g:each in="${sourceInstanceList}" status="i" var="d">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${d.name}">${fieldValue(bean: d, field: "name")}</g:link></td>
						<td>${fieldValue(bean:d, field: "accessMethod") }</td>
						<td>${fieldValue(bean:d, field: "matchingString") }</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${sourceInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
