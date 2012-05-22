<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title><g:message code="osler.mb.tester.index.title" /></title>
		<g:javascript library="jquery" />
		<g:javascript>					
			function submitEvent(eventName, personType, personId, locationId) {
				//alert("eventName=" + eventName + ", personType=" + personType + ", personId=" + personId + ", locationId=" + locationId);
				$("#eventName").val(eventName);
				$("#personType").val(personType);
				$("#personId").val(personId);
				$("#locationId").val(locationId);
				document.forms[0].submit();
			}
		</g:javascript>
		<style>
		#rtls tr th {
			text-align: center;
			width: ${ 70 / (locations.size() * 2)}%;
		}		
		#rtls button {
			height: 5em;	
			width: 100%;
			font-size: 0.8em;
		}
		</style>
	</head>
	<body>
		<div id="page-body" role="main">
			<g:render template="nav"/>
			<div id="body" class="narrow">
				<h2><g:message code="osler.mb.tester.rtlsSimulator.label" /></h2>
				<g:messages/>
				<div><g:message code="osler.mb.tester.rtlsSimulator.help"/></div>				
				<g:form method="POST" action="runRtlsEvent">
					<g:hiddenField name="eventName"/>
					<g:hiddenField name="personType"/>
					<g:hiddenField name="personId"/>
					<g:hiddenField name="locationId"/>
					<table id="rtls">
						<tr>
							<th style="width:30%;">Person</th>
							<g:each in="${locations}" var="l">
								<th>
									In ${l.id}<br/>
									${l.value}
								</th>
								<th>
									Out ${l.id}<br/>
									${l.value}
								</th>
							</g:each>
						</tr>
						<g:each in="${people}" var="p" status="i">						
							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
								<td>
									<g:if test="${p.icon}">
										<img src="${createLinkTo(dir:"images", file: p.icon)}" alt="${p.name}"/>
									</g:if>
									<g:else>
										${p.name}:
									</g:else>
									${p.value}
								</td>
								<g:each in="${locations}" var="l">
									<td><button type="button" onclick="submitEvent('${p.name}In${l.id}','${p.id}','${p.value}','${l.value}');">${p.name}<br/> in ${l.id}</button></td>
									<td><button type="button" onclick="submitEvent('${p.name}Out${l.id}','${p.id}','${p.value}','${l.value}');">${p.name}<br/> out of ${l.id}</button></td>
								</g:each>								
							</tr>
						</g:each>
					</table>
				</g:form>				
			</div>
		</div>
	</body>
</html>
