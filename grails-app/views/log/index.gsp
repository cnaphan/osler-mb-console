<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Log Analytics</title>
		<script type="text/javascript" src="https://www.google.com/jsapi"></script>
		<g:if test="${eventsByHour}">
    	<script type="text/javascript">
      		google.load("visualization", "1", {packages:["corechart"]});
      		google.setOnLoadCallback(drawChart);
      		function drawChart() {
		        var data1 = new google.visualization.DataTable();
		        data1.addColumn('datetime', 'Time');
		        data1.addColumn('number', 'Events');		        
		        data1.addRows([
		       	  <g:each in="${eventsByHour}" var="e" status="i">
		       	  [new Date(${e.value[0]}, ${e.value[1]}, ${e.value[2]}, ${e.value[3]},${e.value[4]},${e.value[5]}), ${e.value[6]}], //${e.key}
		       	  </g:each>
		        ]);
		
		        var options1 = {		          
		          legend: {position: 'none'},
		          chartArea: {width:'90%'},
		          vAxis: {minValue: 0, format: "##"},
		          pointSize: 5,   lineWidth: 0,                 
		        };

		        var chart1 = new google.visualization.LineChart(document.getElementById('events_by_hour'));
		        chart1.draw(data1, options1);
	
		        var data2 = new google.visualization.DataTable();
		        data2.addColumn('string', 'Source');
		        data2.addColumn('number', 'Events');
		        data2.addRows([
		       		<g:each in="${eventsBySource}" var="e" status="i">['${e.key}', ${e.value}],</g:each>
		        ]);
	
		        var options_pie = {
		        	legend: {position: 'right'},
		        	pieSliceText: 'percent',
		        	chartArea: {top: '5%', left: '0', width: '90%', height: '90%'},     
		        };
	
		        var chart3 = new google.visualization.PieChart(document.getElementById('events_by_source'));
		        chart3.draw(data2, options_pie);

		        var data3 = new google.visualization.DataTable();
		        data3.addColumn('string', 'Method');
		        data3.addColumn('number', 'Events');
		        data3.addRows([
		       		<g:each in="${eventsByInput}" var="e" status="i">['${e.key}', ${e.value}],</g:each>
		        ]);
			   
		        var chart3 = new google.visualization.PieChart(document.getElementById('events_by_input'));
		        chart3.draw(data3, options_pie);

		        var data4 = new google.visualization.DataTable();
		        data4.addColumn('string', 'Source');
		        data4.addColumn('number', 'Events');
		        data4.addRows([
		       		<g:each in="${eventsByType}" var="e" status="i">['${e.key}', ${e.value}],</g:each>
		        ]);

		        var options_type = {
			        	legend: {position: 'none'},
			        	chartArea: {left: '25%', width: '90%', height: '90%'},
			        	hAxis: {minValue: 0},
			        };
			   
		        var chart4 = new google.visualization.BarChart(document.getElementById('events_by_type'));
		        chart4.draw(data4, options_type);
	      }
    </script>
    </g:if>
	<style>
	</style>
	</head>
	<body>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="dashboard" action="index"><g:message code="osler.mb.routing.Log.index.title" default="Log Dashboard" /></g:link></li>				
				<li><g:link class="list" action="list"><g:message code="osler.mb.routing.Log.list.title"/></g:link></li>
				<li><g:link class="list" action="responseLogList"><g:message code="osler.mb.routing.Log.responseLogList.title"/></g:link></li>
			</ul>
		</div>
		<div id="body">
			<h1><g:message code="osler.mb.routing.Log.index.title" default="Log Dashboard" /></h1>
			<div style="width: 20%; float: right;">
				<g:form action="index" method="GET">
				View for: <g:select name="viewfor" optionKey="key" optionValue="value" from="${viewByMap}" value="${viewfor}" onchange="document.forms[0].submit()"/>
				</g:form>
			</div>
			<g:if test="${ eventsByHour }">
				<div style="width: 80%; float:left;">
					<h3>Events by Time</h3>		
					<div id="events_by_hour" style="width: 100%; height: 150px;"></div>
				</div>
				<div style="clear:both;"></div>
				<div style="width: 50%; float:left;">
					<h3>Events by Source</h3>
					<div id="events_by_source" style="width: 100%; height: 200px;"></div>
				</div>
				<div style="width: 50%; float: right;">
					<h3>Events by Input Method</h3>
					<div id="events_by_input" style="width: 100%; height: 200px;"></div>
				</div>
				<div style="clear:both;"></div>
				<div style="width: 100%;">
					<h3>Events by Type</h3>
					<div id="events_by_type" style="width: 80%; height: ${eventsByType.size() * 22}px;"></div>
				</div>	
			</g:if>
			<g:else>
				<i>There are no events logged for the time period selected.</i>				
			</g:else>
		</div>
	</body>
</html>
