package osler.mb.routing

import groovy.xml.*

/**
 * Provides an in-memory implementation of the routing rules XML transport, for testing purposes.
 */
class MemXmlTransport extends XmlTransport {
	
	private static final log = org.apache.commons.logging.LogFactory.getLog(this)
	
	private static final String ORIGINAL_FILE = """
<oslerRoutingRules>
	<events>
		<event>patientAdmittedWithBed</event>
		<event>consultationCompleted2</event>
		<event>patientRegistered</event>
		<event>consultationStarted1</event>
		<event>consultationCompleted1</event>
	</events>
	<destinations>
		<destination>
			<name>TestSoap</name>
			<description></description>
			<url>http://fsa4.site.uottawa.ca:8080/osler-mb/receive/soap
			</url>
			<accessMethod>SOAP</accessMethod>
			<format>PFM</format>
			<receives>
				<event>patientAdmittedWithBed</event>
				<event>consultationCompleted2</event>
				<event>patientRegistered</event>
				<event>consultationStarted1</event>
				<event>consultationCompleted1</event>
			</receives>
		</destination>
		<destination>
			<disabled>false</disabled>
			<name>TestTws</name>
			<description></description>
			<url>http://fsa4.site.uottawa.ca:8080/osler-mb/receive/tws
			</url>
			<accessMethod>SOAP</accessMethod>
			<format>TWS</format>
			<receives>
				<event>patientAdmittedWithBed</event>
				<event>consultationCompleted2</event>
				<event>patientRegistered</event>
			</receives>
		</destination>
		<destination>
			<disabled>false</disabled>
			<name>TestRest</name>
			<description></description>
			<url>http://fsa4.site.uottawa.ca/osler-mb/receive/rest
			</url>
			<accessMethod>REST</accessMethod>
			<format>PFM</format>
			<receives>
				<event>patientRegistered</event>
				<event>consultationStarted1</event>
				<event>consultationCompleted1</event>
			</receives>
		</destination>
	</destinations>
	<sources>
		<source>
			<name>FSA4</name>
			<accessMethod>SOAP</accessMethod>
			<matchingString>137.122.93.183</matchingString>
		</source>
		<source>
			<name>dmuhi064</name>
			<accessMethod>SOAP</accessMethod>
			<matchingString>137.122.88.14</matchingString>
		</source>
	</sources>
</oslerRoutingRules>
	"""
	
	private static String mem = null
		
	public groovy.util.slurpersupport.GPathResult getRoutingRules() {
		if (!mem) {
			log.info("Refreshing memory with original file")
			mem = MemXmlTransport.ORIGINAL_FILE
		}
		log.info("Fetching in-memory routing rules")
		return new XmlSlurper(true, false).parseText(mem)
	}

	public boolean updateRoutingRules(def root) {
		def outputBuilder = new StreamingMarkupBuilder()
		String result = outputBuilder.bind{ mkp.yield(root) }
		mem = result
		log.info("Updated in-memory routing rules")
		return true
	}
}
