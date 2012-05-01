<g:if test="${flash.message || flash.error || (flash.messages?.size() > 0) }">
<div id="messages" class="${(!flash.message && !flash.messages?.contains{ it[0] != "error"}) ? "errors" : ""}">
	<ul>
		<g:if test="${flash.error }">
			<li class="error">${flash.error }</li>
		</g:if>
		<g:if test="${flash.message }">
			<li class="info">${flash.message }</li>
		</g:if>
		<g:each in="${flash.messages }" var="m">
			<li class="${m[0]}">${m[1]}</li>
		</g:each>
		<g:eachError>
			<li class="error"><g:message error="${it}"/></li>
		</g:eachError>
	</ul>
</div>
</g:if>
<g:hasErrors>
<div class="errors">
	<g:renderErrors/>
</div>
</g:hasErrors>