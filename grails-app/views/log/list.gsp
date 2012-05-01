
<%@ page import="osler.mb.routing.Log"%>
<!doctype html>
<html>
<head>
<meta name="layout" content="main">
<g:set var="entityName"
	value="${message(code: 'log.label', default: 'Log')}" />
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
			<li><g:link class="list" action="list">
					<g:message code="osler.mb.routing.Log.list.title"
						default="View Log" />
				</g:link></li>
		</ul>
	</div>
	<div id="body" class="narrow">
		<g:form action="list" method="GET">
			<h1>
				<g:message code="osler.mb.routing.log.list.title" default="View Log" />
			</h1>
			<g:messages/>
			<div style="float: left;">
				Returned <strong>
					${logInstanceTotal}
				</strong> entries
			</div>
			<div style="float: right; text-align: right;">
				From:
				<g:textField name="fromdate" value="${params.fromdate}" />
				&nbsp;&nbsp; To: <input type="text" name="todate"
					value="${params.todate}"> Show:
				<g:select name="max" keys="${[10,25,50,100,-1]}"
					from="${[10,25,50,100,"All entries"]}" value="${params.max}" />
				<input type="submit" value="Go" />
			</div>
			<table class="data tight-list">
				<thead>
					<tr>
						<g:sortableColumn property="logTime"
							title="${message(code: 'log.logTime.label', default: 'Log Time')}" rowspan="2"/>
						<g:sortableColumn property="event"
							title="${message(code: 'log.event.label', default: 'Event')}"  rowspan="2"/>
						<g:sortableColumn property="source"
							title="${message(code: 'log.source.label', default: 'Source')}"  rowspan="2"/>
						<g:sortableColumn property="inputMethod"
							title="${message(code: 'log.inputMethod.label', default: 'Input Method')}"  rowspan="2"/>
						<th colspan="2" style="background: #FCFCFC; text-align: center;">Num sent to:</th>
					</tr>
					<tr>
						<g:sortableColumn property="numSentP2P" title="P2P"/>
						<g:sortableColumn property="numSentPubSub" title="PubSub"/>		
					</tr>
				</thead>
				<tbody>
					<g:each in="${logInstanceList}" status="i" var="logInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							<td><g:formatDate date="${logInstance.logTime}"
									format="${logDateFormat}" /></td>
							<td>
								${fieldValue(bean: logInstance, field: "event")}
							</td>
							<td>
								${fieldValue(bean: logInstance, field: "source")}
							</td>
							<td>
								${fieldValue(bean: logInstance, field: "inputMethod")}
							</td>
							<td>
								${fieldValue(bean: logInstance, field: "numSentP2P")}
							</td>
							<td>
								${fieldValue(bean: logInstance, field: "numSentPubSub")}
							</td>
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
