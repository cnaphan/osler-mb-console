package osler.mb.routing

import groovyx.net.http.*
import groovy.xml.*
import org.codehaus.groovy.grails.web.context.ServletContextHolder

/**
 * Provides a set of methods for transporting the XML routing rules to and from the Message Broker using a 
 * RESTful web service. The service end-points are stored in the configuration file.
 * Further reading: http://grails.org/doc/latest/guide/webServices.html#13.1 REST
 * @author cnaph035
 *
 */
class RestXmlTransport extends XmlTransport {


	public groovy.util.slurpersupport.GPathResult getRoutingRules() {				
		def grailsApplication = new Log().domainClass.grailsApplication		
		log.debug("Fetching routing rules from remote service")
		return new HTTPBuilder(grailsApplication.config.osler.mb.getRoutingRulesUrl).request(Method.GET,ContentType.XML) {}		
	}

	public boolean updateRoutingRules(def root) {
		def grailsApplication = new Log().domainClass.grailsApplication	
		// Turn the parsed XML back into a string
		def outputBuilder = new StreamingMarkupBuilder()
		String result = outputBuilder.bind{ mkp.yield(root) }
		try {
			// Write a copy to the local drive
			File f = new File (ServletContextHolder.servletContext.getRealPath('/xml/latest-routing-rules.xml'))
			f.write(result)
		} catch (Exception e) {
			log.warn("Failed to write latest copy of routing rules to web app but continuing... Message: ${e.getMessage()}")
		}
		new HTTPBuilder(grailsApplication.config.osler.mb.updateRoutingRulesUrl).post (body: result) { resp ->
			log.debug("Sent updated routing rules. Responded with status code ${resp.statusLine.statusCode}.")
			return resp.statusLine.statusCode == 200
		}
	}
}