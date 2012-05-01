package osler.mb.routing

import groovy.xml.*

class LocalFileXmlTransport extends XmlTransport {
	
	public String getLocalFilePath() {
		return org.codehaus.groovy.grails.web.context.ServletContextHolder.servletContext.getRealPath('/xml/default-routing-rules.xml')
		
	}
	
	public groovy.util.slurpersupport.GPathResult getRoutingRules() {
		return new XmlSlurper(true, false).parse(this.getLocalFilePath())
	}

	public boolean updateRoutingRules(def root) {
		def outputBuilder = new StreamingMarkupBuilder()
		String result = outputBuilder.bind{ mkp.yield(root) }
		File f = new File (this.getLocalFilePath())
		f.write(result)	
		return true
	}
}