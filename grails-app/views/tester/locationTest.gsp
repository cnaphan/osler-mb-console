<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="osler.mb.tester.index.title" default="Inject Test Events" /></title>
		<g:javascript library="jquery" />
		<g:javascript>
			function changeLocationPersonId(personType) {
				if (personType.value == "patientId") { $("#locationPersonId").val("Pa123456"); }
				else if (personType.value == "providerId") { $("#locationPersonId").val("Pro654321"); }
				else if (personType.value == "physicianId") { $("#locationPersonId").val("Phy777777"); }			
			}
		</g:javascript>
	</head>
	<body>
		<a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="page-body" role="main">
			<div class="nav" role="navigation">
				<ul>
					<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link action="index"><g:message code="default.button.back.label"/></g:link></li>
				</ul>
			</div>
			<div class="content scaffold-edit" role="main">
				<h1><g:message code="osler.mb.tester.locationTest.label" /></h1>
				<div><g:message code="osler.mb.tester.locationTest.help"/></div>
				<g:if test="${flash.message}">
					<div class="message" role="status">${flash.message}</div>
				</g:if>
				<g:form method="POST">
					<fieldset>
						<div class="fieldcontain">
							<label for="personId">
								<g:select name="locationPersonType" from="${["patientId","providerId","physicianId"] }" onchange="changeLocationPersonId(this)"/>								
							</label>
							<g:textField name="locationPersonId" value="Pa123456"/>					
						</div>
						<div class="fieldcontain">
							<label for="locationEventName">
								<g:message code="osler.mb.tester.locationEventName.label" />
								<span class="required-indicator">*</span>
							</label>
							<g:textField name="locationEventName" value="${defaultEventName }"/>					
						</div>
						<div class="fieldcontain">
							<label for="locationId">
								<g:message code="osler.mb.tester.locationId.label"  />
								<span class="required-indicator">*</span>
							</label>
							<g:textField name="locationId" value="${defaultLocationId }"/>					
						</div>						
					</fieldset>
					<fieldset class="buttons">
						<g:actionSubmit class="run" action="runLocationTest" value="${message(code: 'default.button.run.label')}" />
					</fieldset>
				</g:form>				
			</div>
		</div>
	</body>
</html>
