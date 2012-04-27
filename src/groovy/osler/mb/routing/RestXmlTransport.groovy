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

	private final static String CACHE_KEY = "oslerRoutingRules"
	private final static Integer CACHE_DURATION_MINUTES = 5	

	public groovy.util.slurpersupport.GPathResult getRoutingRules() {				
		def grailsApplication = new Log().domainClass.grailsApplication		
		def cachedRr = grailsApplication.mainContext.getServletContext().getAttribute(CACHE_KEY)
		if (!cachedRr || new GregorianCalendar().compareTo(cachedRr[0]) >= 0) {			
			log.debug("Fetching routing rules from remote service")
			def rr = new HTTPBuilder(grailsApplication.config.osler.mb.getRoutingRulesUrl).request(Method.GET,ContentType.XML) {}
			this.setCachedRules(rr)
			return rr
		} else {
			log.debug("Fetching routing rules from servlet context")
			return cachedRr[1]
		}	
	}

	public boolean updateRoutingRules(def root) {
		def grailsApplication = new Log().domainClass.grailsApplication	
		// Turn the parsed XML back into a string
		def outputBuilder = new StreamingMarkupBuilder()
		String result = outputBuilder.bind{ mkp.yield(root) }
		
		new HTTPBuilder(grailsApplication.config.osler.mb.updateRoutingRulesUrl).post (body: result) { resp ->
			log.debug("Sent updated routing rules. Responded with status code ${resp.statusLine.statusCode}.")
			this.setCachedRules(root) // Touch the cached copy
			return resp.statusLine.statusCode == 200
		}
	}
	
	/**
	 * Sets the routing rules into the cached for a certain duration. Uses the servlet context as a sort of cache.
	 * Probably not the best way.
	 * @param rr
	 */
	private void setCachedRules(def rr) {
		def now = new GregorianCalendar()
		now.add(Calendar.MINUTE, CACHE_DURATION_MINUTES) // Cache the routing rules for a few minutes
		new Log().domainClass.grailsApplication.mainContext.getServletContext().setAttribute(CACHE_KEY, (Object)[now, rr])
	}
}