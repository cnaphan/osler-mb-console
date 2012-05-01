<div style="float: right; text-align: right;">
	From:
	<g:textField name="fromdate" value="${params.fromdate}" />
	&nbsp;&nbsp; To: <input type="text" name="todate"
		value="${params.todate}"> Show:
	<g:select name="max" keys="${[10,25,50,100,-1]}"
		from="${[10,25,50,100,"All entries"]}" value="${params.max}" />
	<input type="submit" value="Go" />
</div>
