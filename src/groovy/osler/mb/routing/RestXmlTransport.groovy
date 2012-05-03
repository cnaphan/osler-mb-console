package osler.mb.routing

import groovyx.net.http.*
import groovy.xml.*
import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH

/**
 * Provides a set of methods for transporting the XML routing rules to and from the Message Broker using a 
 * RESTful web service. The service end-points are stored in the configuration file.
 * Further reading: http://grails.org/doc/latest/guide/webServices.html#13.1 REST
 *
 * This implementation uses caching to store the routing rules for 5 minutes at a time. There are two reasons. First,
 * it is faster. Second, Message Broker is too slow to update the routing rules before the console's next page loads,
 * resulting in the user not seeing the changes for a few seconds.
 * @author cnaph035
 *
 */
class RestXmlTransport extends XmlTransport {
	
	private static final log = org.apache.commons.logging.LogFactory.getLog(this)
	
	private final static String NEXT_TIME_KEY = "NextTime"
	private final static Integer USE_LOCAL_FOR_MINUTES = 5
	private final static String PATH_TO_LATEST_COPY = "/xml/latest-routing-rules.xml"
		
	public groovy.util.slurpersupport.GPathResult getRoutingRules() {				
		def grailsApplication = new Log().domainClass.grailsApplication		
		def cacheDate = SCH.servletContext.getAttribute(NEXT_TIME_KEY)
		if (!cacheDate || new Date().compareTo(cacheDate) > 0) {
			def c = new GregorianCalendar()
			c.add(Calendar.MINUTE, USE_LOCAL_FOR_MINUTES)
			SCH.servletContext.setAttribute(NEXT_TIME_KEY, c.getTime())
			log.debug("Fetching routing rules from remote service")
			return new HTTPBuilder(grailsApplication.config.osler.mb.getRoutingRulesUrl).request(Method.GET,ContentType.XML) {}
		} else {
			log.debug("Fetching routing rules from local file")
			return new XmlSlurper(true, false).parse(SCH.servletContext.getRealPath(PATH_TO_LATEST_COPY))
		}
	}

	public boolean updateRoutingRules(def root) {
		def grailsApplication = new Log().domainClass.grailsApplication	
		// Turn the parsed XML back into a string
		def outputBuilder = new StreamingMarkupBuilder()
		String result = outputBuilder.bind{ mkp.yield(root) }
		try {
			// Write a copy to the local drive
			File f = new File (SCH.servletContext.getRealPath(PATH_TO_LATEST_COPY))
			f.write(result)
		} catch (Exception e) {
			log.debug("Failed to write latest copy of routing rules to web app but continuing... Message: ${e.getMessage()}")
		}
		new HTTPBuilder(grailsApplication.config.osler.mb.updateRoutingRulesUrl).post (body: result) { resp ->
			if (resp.statusLine.statusCode == 200) {
				log.debug("Sent updated routing rules. Responded with status code ${resp.statusLine.statusCode}.")
				return true
			} else {
				log.error("Could not update remote routing rules. Returned ${resp.statusLine.statusCode}.")
				return false
			}
		}
	}
}