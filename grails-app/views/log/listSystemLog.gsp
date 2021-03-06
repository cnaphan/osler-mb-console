
<%@ page import="osler.mb.routing.Log"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'log.label', default: 'System Log')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	<g:render template="nav"/>
	<div id="body">
		<g:form action="list" method="GET">
			<h1>System Log</h1>
			<g:messages/>
			The log entries generated by the console loggers.
			<table class="data tight-list">
				<thead>
					<tr>
						<g:sortableColumn property="dated" title="Date"/>
						<g:sortableColumn property="userId" title="User"/>
						<g:sortableColumn property="logger" title="Logger"/>
						<g:sortableColumn property="level" title="Level"/>
						<th style="width:50%;">Message</th>
					</tr>
				</thead>
				<tbody>
					<g:each in="${logInstanceList}" status="i" var="logInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							<td><g:formatDate date="${logInstance.dated}" format="${grailsApplication.config.osler.mb.dateFormat}"/></td>							
							<td>${logInstance.userId}</td>
							<td>${logInstance.logger}</td>
							<td>${logInstance.level}</td>
							<td>${logInstance.message}</td>
						</tr>
					</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${logInstanceTotal}" params="${params}" />
			</div>
		</g:form>
	</div>

</body>
</html>
