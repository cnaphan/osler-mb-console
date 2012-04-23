
<%@ page import="osler.mb.routing.Log" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'log.label', default: 'Log')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-log" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="dashboard" action="index"><g:message code="osler.mb.routing.Log.index.title" default="Log Dashboard" /></g:link></li>				
				<li><g:link class="list" action="list"><g:message code="osler.mb.routing.Log.list.title" default="View Log" /></g:link></li>
			</ul>
		</div>	
		<g:form action="list" method="GET">
		<div id="list-log" class="content tight-list" role="main">
			<h1><g:message code="osler.mb.routing.log.list.title" default="View Log" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<div style="float:left;">
				Returned <strong>${logInstanceTotal}</strong> entries
			</div>
			<div style="float:right; text-align: right;">
				From: <g:textField name="fromdate" value="${params.fromdate}" />&nbsp;&nbsp;
				To: <input type="text" name="todate" value="${params.todate}">
				Show: <g:select name="max" keys="${[10,25,50,100,-1]}" from="${[10,25,50,100,"All entries"]}" value="${params.max}" />
				<input type="submit" value="Go"/>
			</div>
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="logTime" title="${message(code: 'log.logTime.label', default: 'Log Time')}" />					
						<g:sortableColumn property="event" title="${message(code: 'log.event.label', default: 'Event')}" />					
						<g:sortableColumn property="source" title="${message(code: 'log.source.label', default: 'Source')}" />					
						<g:sortableColumn property="inputMethod" title="${message(code: 'log.inputMethod.label', default: 'Input Method')}" />									
					</tr>
				</thead>
				<tbody>
				<g:each in="${logInstanceList}" status="i" var="logInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
						<td><g:formatDate date="${logInstance.logTime}" format="${logDateFormat}"/></td>					
						<td>${fieldValue(bean: logInstance, field: "event")}</td>					
						<td>${fieldValue(bean: logInstance, field: "source")}</td>					
						<td>${fieldValue(bean: logInstance, field: "inputMethod")}</td>
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${logInstanceTotal}" params="${params}"/>
			</div>
		</div>
		</g:form>	
	</body>
</html>
