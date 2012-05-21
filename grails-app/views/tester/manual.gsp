<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="osler.mb.tester.index.title" default="Inject Test Events" /></title>
		<style type="text/css">
		.xml-body {
			height: 4em;
			width: 60em;			
		}
		.sent {
			color: green;
			font-weight: bold;
		}
		</style>
		<g:javascript library="jquery" />
		<g:javascript>
			function setErrorVisible(isVisible) {				
				$("#send-failure").css("display", isVisible ? "block" : "none");
			}
		</g:javascript>
	</head>
	<body>
		<div id="page-body" role="main">
			<g:render template="nav"/>
			<div id="body">
				<g:messages/>
				<h1><g:message code="osler.mb.tester.manual.title" /></h1>
				<div id="intro"><g:message code="osler.mb.tester.manual.help"/></div>				
				<div id="send-failure" class="errors" style="display: none;"></div>
				<table class="data">
					<thead>
						<tr>
							<th style="width: 2em;">#</th>
							<th>Event Name</th>
							<th>Body</th>
							<th>&nbsp;</th>									
						</tr>
					</thead>
					<tbody>
					<g:each in="${eventList}" status="i" var="e">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							<td>${i+1}</td>					
							<td>${e.method}</td>
							<td><textarea class="xml-body" readonly="readonly">${e.body}</textarea></td>
							<td style="text-align: center; vertical-align: middle;" id='send${i}'>
							 	<g:formRemote name="sendForm.${i}" url="[controller: 'tester', action: 'send']" method="POST" update="${[success: 'send'+i, failure: 'send-failure'] }" onFailure="setErrorVisible(true);" onSuccess="setErrorVisible(false);">
							 		<g:hiddenField name="method" value="${e.method}"/>
							 		<g:hiddenField name="body" value="${e.body}"/>
							 		<g:submitButton name="send" value="${message(code:'osler.mb.tester.manual.send.label')}"/>
							 	</g:formRemote>
							</td>												
						</tr>
					</g:each>
					</tbody>
				</table>									
				<fieldset class="buttons">
					<g:link controller="tester" action="index"><g:message code="default.button.done.label"/></g:link>
				</fieldset>				
			</div>
		</div>
	</body>
</html>
