<%@ page import="osler.mb.routing.Source" %>

<div class="fieldcontain ${hasErrors(bean: sourceInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="osler.mb.routing.Source.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" maxlength="50" required="" value="${sourceInstance?.name}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: sourceInstance, field: 'accessMethod', 'error')} required">
	<label for="url">
		<g:message code="osler.mb.routing.Source.accessMethod.label" />
	</label>	
	<g:textField name="accessMethod" maxlength="10" required="" value="${sourceInstance?.accessMethod}"/>	
	(SOAP, REST, etc...)	
<</div>
<div class="fieldcontain ${hasErrors(bean: sourceInstance, field: 'matchingString', 'error')} required">
	<label for="matchingString">
		<g:message code="osler.mb.routing.Source.matchingString.label" default="Matching String" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="matchingString" maxlength="50" required="" value="${sourceInstance?.matchingString}"/>	
</div>
