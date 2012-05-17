<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="osler.mb.tester.index.title" /></title>
		<g:javascript library="jquery" />
	</head>
	<body>
		<div id="page-body" role="main">
			<div class="nav" role="navigation">
				<ul>
					<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><a class="" href="${createLink(action: 'index')}"><g:message code="osler.mb.tester.index.title"/></a></li>
					<li><a class="" href="${createLink(action: 'locationTest')}"><g:message code="osler.mb.tester.locationTest.label"/></a></li>
					<li><a class="" href="${createLink(action: 'integrationTest')}"><g:message code="osler.mb.tester.integrationTest.label"/></a></li>
				</ul>
			</div>
			<div style="float: right; width: 16em; margin: 0.5em; padding: 0.5em; border: 1px solid #DDD;">
				<h3>XML Resources</h3>
				<ul style="list-style-position: inside;">
					<g:each in="${xmlResources}" var="x"><li><a href="${resource(dir: 'xml', file: x)}">${x}</a></li></g:each>
				</ul>
			</div>			
			<div id="body" class="narrow">
				<h1><g:message code="osler.mb.tester.integrationTest.label" /></h1>
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
