package osler.mb.routing

import groovyx.net.http.*
import groovy.xml.*

/**
 * Provides a set of methods for transporting the XML routing rules to and from the Message Broker using a 
 * RESTful web service. The service end-points are stored in the configuration file.
 * Further reading: http://grails.org/doc/latest/guide/webServices.html#13.1 REST
 * @author cnaph035
 *
 */
class RestXmlTransport extends XmlTransport {

	public groovy.util.slurpersupport.GPathResult getRoutingRules() {		
		def http = new HTTPBuilder(grailsApplication.config.osler.mb.getRoutingRulesUrl)
		def rr = http.get() { resp ->
			log.info("Fetched routing rules from web service. Responded with status code ${resp.statusLine.statusCode}.")
		}
		return rr
	}

	public boolean updateRoutingRules(def root) {
		// Turn the parsed XML back into a string
		def outputBuilder = new StreamingMarkupBuilder()
		String result = outputBuilder.bind{ mkp.yield(root) }
		
		// Create an HTTP connection object
		def http = new HTTPBuilder(grailsApplication.config.osler.mb.updateRoutingRulesUrl)
		http.post (body: result) { resp ->
			log.info("Sent updated routing rules. Responded with status code ${resp.statusLine.statusCode}.")
			return resp.statusLine.statusCode == 200
		}
		
	}
}