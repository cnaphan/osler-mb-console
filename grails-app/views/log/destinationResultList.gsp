
<%@ page import="osler.mb.routing.ResponseLog"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'log.label', default: 'Destination Result Log')}" />
<title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
	<div class="nav" role="navigation">
		<ul>
			<li><a class="home" href="${createLink(uri: '/')}"><g:message
						code="default.home.label" /></a></li>
			<li><g:link class="dashboard" action="index">
					<g:message code="osler.mb.routing.Log.index.title"
						default="Log Dashboard" />
				</g:link></li>
				<li><g:link class="list" action="list"><g:message code="osler.mb.routing.Log.list.title"/></g:link></li>
				<li><g:link class="list" action="responseLogList"><g:message code="osler.mb.routing.Log.responseLogList.title"/></g:link></li>
				<li><g:link class="list" action="destinationResultList"><g:message code="osler.mb.routing.Log.destinationResultList.title"/></g:link></li>
		</ul>
	</div>
	<div id="body">
		<g:form action="destinationResultList" method="GET">
			<h1>
				<g:message code="osler.mb.routing.Log.destinationResultList.title" />
			</h1>
			<g:messages/>
			<div style="float: left;">
				Returned <strong>
					${resultInstanceTotal}
				</strong> entries
			</div>
			<g:render template="dates"/>
			<table class="data tight-list">
				<thead>
					<tr>
						<g:sortableColumn property="logTime" title="${message(code: 'log.logTime.label', default: 'Log Time')}"/>
						<g:sortableColumn property="event" title="${message(code: 'log.event.label', default: 'Event')}"/>
						<g:sortableColumn property="method" title="Receiver"/>
						<th>Errors</th>			
					</tr>
				</thead>
				<tbody>
					<g:each in="${resultInstanceList}" status="i" var="r">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							<td><g:formatDate date="${r.logTime}"
									format="${logDateFormat}" /></td>
							<td>
								${fieldValue(bean: r, field: "event")}
							</td>
							<td>
								${fieldValue(bean: r, field: "method")}
							</td>
							<td style="width: 50%; font-size: 0.8em;">
								<g:if test="${r.errorXml}">
									<g:each in="${new XmlSlurper(false,false).parseText(r.errorXml).entry}" var="e">
										<li>${e.@key} &mdash; ${e.text()}</li>
									</g:each>									
								</g:if>
								<g:else>
									<i>None</i>
								</g:else>
								
							</td>
						</tr>
					</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${resultInstanceTotal}" params="${params}" />
			</div>
		</g:form>
	</div>
</body>
</html>
