<%@ page import="osler.mb.routing.Destination" %>

<div class="fieldcontain ${hasErrors(bean: destinationInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="osler.mb.routing.Destination.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>	
	<g:textField name="name" maxlength="50" required="" value="${destinationInstance?.name}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: destinationInstance, field: 'description', 'error')}">
	<label for="url">
		<g:message code="osler.mb.routing.Destination.description.label" default="Description" />
	</label>
	<g:textArea name="description" value="${destinationInstance?.description}" style="height: 4em;width: 30em;"/>	
</div>
<div class="fieldcontain ${hasErrors(bean: destinationInstance, field: 'url', 'error')}">
	<label for="url">
		<g:message code="osler.mb.routing.Destination.url.label" />
	</label>
	<g:textField name="url" maxlength="150" value="${destinationInstance?.url}" style="width: 30em;"/>	
</div>
<div class="fieldcontain ${hasErrors(bean: destinationInstance, field: 'accessMethod', 'error')} required">
	<label for="accessMethod">
		<g:message code="osler.mb.routing.Destination.accessMethod.label" />
	</label>	
	<g:textField name="accessMethod" maxlength="10" required="" value="${destinationInstance?.accessMethod}"/>
	(SOAP, REST, JMS, MQ, PubSub, etc...)	
</div>
<div class="fieldcontain ${hasErrors(bean: destinationInstance, field: 'format', 'error')} required">
	<label for="format">
		<g:message code="osler.mb.routing.Destination.format.label" />
	</label>	
	<g:textField name="format" maxlength="10" required="" value="${destinationInstance?.format}"/>
	(TWS, PFM, WBE, AMQ, etc...)	
</div>
<div class="fieldcontain ${hasErrors(bean: destinationInstance, field: 'disabled', 'error')}">
	<label for="url">
		<g:message code="osler.mb.routing.Destination.disabled.label" />
	</label>	
	<g:checkBox name="disabled" value="true" checked="${ destinationInstance.disabled }"/>	
</div>