
<%@ page import="osler.mb.routing.Source" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'source.label', default: 'Source')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-source" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>		
		<div id="show-source" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${sourceInstance}">
				<ul class="errors" role="alert">
					<g:eachError bean="${sourceInstance}" var="error">
					<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
					</g:eachError>
				</ul>
			</g:hasErrors>
			<ol class="property-list source">			
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="osler.mb.routing.Source.name.label" default="Name" /></span>					
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${sourceInstance}" field="name"/></span>
					
				</li>
				<li class="fieldcontain">
					<span id="accessMethod-label" class="property-label"><g:message code="osler.mb.routing.Source.accessMethod.label" default="Access Method" /></span>	
					<span class="property-value" aria-labelledby="accessMethod-label"><g:fieldValue bean="${sourceInstance}" field="accessMethod"/></span>					
				</li>
				<li class="fieldcontain">
					<span id="matchingString-label" class="property-label"><g:message code="osler.mb.routing.Source.matchingString.label" default="Matching String" /></span>	
					<span class="property-value" aria-labelledby="matchingString-label"><g:fieldValue bean="${sourceInstance}" field="matchingString"/></span>					
				</li>				
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${sourceInstance?.name}" />
					<g:link class="edit" action="edit" id="${sourceInstance?.name}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
