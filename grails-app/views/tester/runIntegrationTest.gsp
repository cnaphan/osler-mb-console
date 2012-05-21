<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="osler.mb.tester.index.title" /></title>
		<g:javascript library="jquery" />
	</head>
	<body>
		<div id="page-body" role="main">
			<g:render template="nav"/>
			<div id="body" class="narrow">
				<h1><g:message code="osler.mb.tester.integrationTestResults.label" /></h1>
				<div id="results" style="background-color: ${whatHappened.find { it.type?.equals("error") } ? '#FF5353' : '#B3FF99'}; border: 1px solid black; padding: 1em; margin: 1em 0; font-weight: bold; font-size: 1.1em; width: 100%;">
					<g:if test="${ whatHappened.find { it.type?.equals("error") } }">
						Test failed.
					</g:if>
					<g:elseif test="${ whatHappened.find { it.type?.equals("warn") } }">
						Test passed but with warnings. Check the details for more information.
					</g:elseif>
					<g:else>
						Test passed.
					</g:else>
				</div>
				<div id="messages" style="background: none; padding: 1em; margin: 1em 0; width: 100%;">
					Details:
					<ul>
					<g:each in="${whatHappened}" var="w">
						<li class="${w.type}">${w.text}</li>
					</g:each>
					</ul>
				</div>
				<g:link action="integrationTest"><g:message code="default.button.back.label"/></g:link>
			</div>
		</div>
	</body>
</html>
