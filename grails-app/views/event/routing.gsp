<%@ page import="osler.mb.routing.*" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'event.label', default: 'Event')}" />
		<g:set var="destinationName" value="${message(code: 'destination.label', default: 'Destination')}" />
		<title><g:message code="osler.mb.routing.EventRouting.title"/></title>
		<g:javascript library="jquery" />
		<g:javascript>
			function toggleAll(allCheck, destinationName) {				
				$('input[name^="paths.' + destinationName + '."]').each(function(index) {
					$(this).attr("checked", allCheck.checked);
				});
			}
		</g:javascript>
	</head>
	<body>
		<a href="#list-event" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" controller="event" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="list" controller="destination" action="list"><g:message code="default.list.label" args="[destinationName]" /></g:link></li>
				<li><g:link class="list" controller="source" action="list"><g:message code="default.list.label" args="['Source']" /></g:link></li>
			</ul>
		</div>
		<div id="list-event" class="content tight-list" role="main">
			<h1><g:message code="osler.mb.routing.EventRouting.title"/></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:form method="post" >
				<table>
					<thead>
						<tr>						
							<th><g:message code="osler.mb.routing.Event.label" default="Event"/></th>
							<g:each in="${destinationInstanceList}" var="d">
								<th>
									${d.name}<br/>
									<input type="checkbox" name="all.${d.name}" ${(d.events.size() == eventInstanceList.size()) ? 'checked="checked"' : '' } onchange="toggleAll(this, '${d.name}')"/>
								</th>
							</g:each>
						</tr>
					</thead>
					<tbody>
					<g:each in="${eventInstanceList}" status="i" var="e">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">					
							<td class="first-column">${fieldValue(bean: e, field: "name")}</td>
							<g:each in="${destinationInstanceList}" status="j" var="d">
								<td><input type="checkbox" name="paths.${d.name}.${e.name}" id="paths.${d.name}.${e.name}"  value="on" ${d.events.contains(e.name)?'checked="checked"':''} /></td>
							</g:each>					
						</tr>
					</g:each>
					</tbody>
				</table>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="updaterouting" value="${message(code: 'osler.mb.routing.EventRouting.update', default: 'Update')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
