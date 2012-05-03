package osler.mb.routing

import groovy.xml.*
import org.codehaus.groovy.grails.web.context.ServletContextHolder

class LocalFileXmlTransport extends XmlTransport {
	
	private static final log = org.apache.commons.logging.LogFactory.getLog(this)
	
	private String getLocalFilePath() {
		return ServletContextHolder.servletContext.getRealPath('/xml/default-routing-rules.xml.bak')		
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