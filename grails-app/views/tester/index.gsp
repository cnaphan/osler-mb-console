<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="osler.mb.tester.index.title" default="Inject Test Events" /></title>		
	</head>
	<body>
		<a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="page-body" role="main">
			<div class="nav" role="navigation">
				<ul>
					<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link action="locationTest"><g:message code="osler.mb.tester.locationTest.label"/></g:link></li>
				</ul>
			</div>
			<div class="content scaffold-edit" role="main">
				<h1><g:message code="osler.mb.tester.index.title" default="Inject Test Events" /></h1>
				<g:if test="${flash.errors}">
				<ul class="errors" role="alert">
					<g:each in="${flash.errors}" var="error">
					<li>${error}</li>
					</g:each>
				</ul>
				</g:if>					
				<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
				</g:if>
				<g:if test="${xmlResources}">
				<div style="float: right; width: 16em; margin: 0.5em; padding: 0.5em; border: 1px solid #AAA; font-size: 75%;">
					<h4>XML Resources</h4>
					<ul style="list-style-position: inside;">
						<g:each in="${xmlResources}" var="x"><li><a href="${resource(dir: 'xml', file: x)}">${x}</a></li></g:each>
					</ul>
				</div>
				</g:if>
				<g:uploadForm method="POST">				
					<fieldset class="form">
						<div class="fieldcontain">
							<label for="filename">
								<g:message code="osler.mb.tester.testScript.label" default="Test Script" />
								<span class="required-indicator">*</span>
							</label>
							<input type="file" name="testScript" />						
						</div>					
						<div class="fieldcontain">
							<label for="overrideTimestamps">
								<g:message code="osler.mb.tester.overrideTimestamps.label" default="Override Timestamps" />							
							</label>
							<g:checkBox name="overrideTimestamps" checked="true"/> <g:message code="osler.mb.tester.overrideTimestamps.help" />
						</div>
						<div class="fieldcontain">
							<label for="mode" style="vertical-align: top;"><g:message code="osler.mb.tester.mode.label"/></label>
							<div style="display: inline-block; width: 30em; font-size: smaller;">
							<g:radioGroup values="${modeList.keySet()}" labels="${modeList.values().toList()}" name="mode" value="1">
								${it.radio} <g:message code="${it.label}"/><br/>
							</g:radioGroup>
							</div>
						</div>
					</fieldset>
					<fieldset class="buttons">
						<g:actionSubmit class="run" controller="tester" action="run" value="${message(code: 'osler.mb.tester.runScript.label', default: 'Run Test Script')}" />
					</fieldset>
				</g:uploadForm>
			</div>
		</div>
	</body>
</html>
