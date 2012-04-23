package osler.mb.routing

import groovy.xml.*

class LocalFileXmlTransport extends XmlTransport {
	
	public String getLocalFilePath() {
		//return System.properties['base.dir'] + "/web-app/xml/default-routing-rules.xml"
		return "./web-app/xml/default-routing-rules.xml"
	}
	
	public groovy.util.slurpersupport.GPathResult getRoutingRules() {
		return new XmlSlurper(true, false).parse(this.getLocalFilePath())
	}

	public boolean updateRoutingRules(def root) {
		def outputBuilder = new StreamingMarkupBuilder()
		String result = outputBuilder.bind{ mkp.yield(root) }
		File f = new File (this.getLocalFilePath())
		f.write('<?xml version="1.0" encoding="utf-8"?>\n\n' + result)		
		return true
	}
}