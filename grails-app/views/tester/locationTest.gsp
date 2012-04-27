<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="osler.mb.tester.index.title" default="Inject Test Events" /></title>
		<g:javascript library="jquery" />
		<g:javascript>
			function changeLocationPersonId(personType) {
				if (personType.value == "patientId") { $("#personId").val("Pa123456"); }
				else if (personType.value == "providerId") { $("#personId").val("Pro654321"); }
				else if (personType.value == "physicianId") { $("#personId").val("Phy777777"); }			
			}
		</g:javascript>
	</head>
	<body>
		<div id="page-body" role="main">
			<div class="nav" role="navigation">
				<ul>
					<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link action="index"><g:message code="default.button.back.label"/></g:link></li>
				</ul>
			</div>
			<div id="body" class="narrow">
				<h1><g:message code="osler.mb.tester.locationTest.label" /></h1>
				<g:messages/>
				<div><g:message code="osler.mb.tester.locationTest.help"/></div>				
				<g:form method="POST">
					<fieldset>
						<div class="fieldcontain">
							<label for="locationEventName">
								<g:message code="osler.mb.tester.locationEventName.label" />
								<span class="required-indicator">*</span>
							</label>
							<g:textField name="eventName" value="${eventName }"/>					
						</div>
						<div class="fieldcontain">
							<label for="personId">
								<g:select name="personType" from="${["patientId","providerId","physicianId"] }" onchange="changeLocationPersonId(this)" value="${personType }"/>								
							</label>
							<g:textField name="personId" value="${personId }"/>					
						</div>
						<div class="fieldcontain">
							<label for="locationId">
								<g:message code="osler.mb.tester.locationId.label"  />
								<span class="required-indicator">*</span>
							</label>
							<g:textField name="locationId" value="${locationId }"/>					
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
