
<%@ page import="osler.mb.routing.Destination" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'destination.label', default: 'Destination')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-destination" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>		
		<div id="body" class="narrow">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:messages/>
			<p><g:message code="osler.mb.routing.Destination.help"/></p>
			<ol class="property-list destination">			
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="osler.mb.routing.Destination.name.label" default="Name" /></span>					
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${destinationInstance}" field="name"/></span>
					
				</li>
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="osler.mb.routing.Destination.description.label" default="Description" /></span>	
					<span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${destinationInstance}" field="description"/></span>					
				</li>
				<li class="fieldcontain">
					<span id="method-label" class="property-label"><g:message code="osler.mb.routing.Destination.method.label" default="Access Method" /></span>	
					<span class="property-value" aria-labelledby="method-label">${destinationInstance.accessMethod}</span>					
				</li>
				<li class="fieldcontain">
					<span id="method-label" class="property-label"><g:message code="osler.mb.routing.Destination.format.label" /></span>	
					<span class="property-value" aria-labelledby="method-label">${destinationInstance.format}</span>					
				</li>
				<li class="fieldcontain">
					<span id="url-label" class="property-label"><g:message code="osler.mb.routing.Destination.url.label" default="URL" /></span>
					<g:if test="${destinationInstance?.url}">	
					<span class="property-value" aria-labelledby="url-label"><a href="${destinationInstance?.url}"><g:fieldValue bean="${destinationInstance}" field="url"/></a></span>
					</g:if>					
				</li>
				<li class="fieldcontain">
					<span id="disabled-label" class="property-label"><g:message code="osler.mb.routing.Destination.disabled.label" default="Disabled" /></span>
					<span class="property-value" aria-labelledby="url-label">${ destinationInstance.disabled ? "Yes" : "No" }</span>										
				</li>
				<g:if test="${destinationInstance?.events}">
					<li class="fieldcontain">
						<span id="events-label" class="property-label"><g:message code="osler.mb.routing.Destination.events.label" default="Receives"/></span>
						<span class="property-value ${hasErrors(bean: destinationInstance, field: 'events', 'error')}" aria-labelledby="events-label">							
							${destinationInstance.events.size()} event${destinationInstance.events.size() != 1 ? "s" : "" }
						</span>
					</li>
				</g:if>
				
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${destinationInstance?.name}" />
					<g:link class="edit" action="edit" id="${destinationInstance?.name}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
