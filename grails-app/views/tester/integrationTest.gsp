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
			<div style="float: right; width: 16em; margin: 0.5em; padding: 0.5em; border: 1px solid #DDD;">
				<h3>XML Resources</h3>
				<ul style="list-style-position: inside;">
					<g:each in="${xmlResources}" var="x"><li><a href="${resource(dir: 'xml', file: x)}">${x}</a></li></g:each>
				</ul>
			</div>			
			<div id="body" class="narrow">
				<h2><g:message code="osler.mb.tester.integrationTest.label" /></h2>
				<g:messages/>
				<div><g:message code="osler.mb.tester.integrationTest.help"/></div>				
				<g:uploadForm method="POST">				
					<fieldset class="form">
						<div class="fieldcontain">
							<label for="filename">
								<g:message code="osler.mb.tester.testScript.label" default="Test Script" />
								<span class="required-indicator">*</span>
							</label>
							<input type="file" name="testScript" />						
						</div>						
					</fieldset>
					<fieldset class="buttons">
						<g:actionSubmit class="run" controller="tester" action="runIntegrationTest" value="${message(code: 'osler.mb.tester.runIntegrationTest.label')}" />
					</fieldset>
				</g:uploadForm>				
			</div>
		</div>
	</body>
</html>
