package osler.mb

import org.xml.sax.SAXException
import osler.mb.routing.*
import java.text.SimpleDateFormat
import java.util.Map;

class TesterController {
	
	static allowedMethods = [index: "GET", run: "POST", send: "POST", locationTest: "GET", runLocationTest: "POST"]
	
	private static final String MODE_MANUAL = 1
	private static final String MODE_AUTO = 2
	private static final Map modeList = [(MODE_MANUAL):"osler.mb.tester.mode.manual.label",
										 (MODE_AUTO):"osler.mb.tester.mode.auto.label"]


	/**
	 * Renders the initial test script page.				
	 */
    def index() {
		if (!params.mode) { params.mode = MODE_MANUAL } // Default mode is 'auto'
				
		def xmlResources = ["default-osler-test-script.xml",
						"aladdin-test-script.xml",
						"oslerTestScript.dtd"]
					
		[xmlResources:xmlResources, modeList: modeList]
	}
	
	/**
	 * Processes an uploaded test script. Extracts events. If using auto mode,
	 * immediately sends all the events to message broker. If using manual mode,
	 * renders the manual.gsp page so that the user can control the timing.
	 */
	def run () { 		
		flash.errors = []		
		def f = request.getFile("testScript")	
		boolean overrideTimestamps = (request.getParameter("overrideTimestamps") != null)
				
		if (!f || f.isEmpty()) {
			// Check if the file uploaded was empty
			log.info("User uploaded empty test script '${f?.getName()}'")
			flash.errors << message(code: "osler.mb.tester.run.empty.message")			
			redirect(view:"index")
			return
		}
		
		ByteArrayInputStream inStream = f.getInputStream()		
		List events = this.parseEvents(inStream, overrideTimestamps)
		if (!events) {
			flash.errors << message(code: "osler.mb.tester.run.empty.message")
		}
		if (flash.errors) {
			redirect(view:"index")
			return
		}
		
		switch (params.mode) {
			case MODE_AUTO:
				flash.errors = []
				// Grab the total number of events in the script for logging and error-checking
				Integer numEvents = events.size()
				Integer numEventsSent = 0 // Used to manually count the events sent, to detect errors
		
				for (def it : events) {		
					Integer responseCode = 0
					try {
						responseCode = this.sendMessage(it.method, it.body)																					
					} catch (java.net.UnknownHostException uhe) {
						// If the host was not found (the MB server was likely down)
						log.warn("Could not establish connection to MB: ${uhe.getMessage()}")
						flash.errors << message(code: "osler.mb.tester.run.soapHostNotFound.message", args:[grailsApplication.config.osler.mb.mbHost])
						break
					}
					// Test the error code and handle any errors
					if (responseCode == 200) { // 200=OK
						numEventsSent = numEventsSent + 1
						if (log.isDebugEnabled()) { log.debug("Event '${it.method}' successfully sent") }
					} else {
						// If there's a non-OK code, log it and report it to the user
						log.warn("SOAP call reported failure: code=${responseCode}'")
						flash.errors << message(code: "osler.mb.tester.run.soapError.message", args:[responseCode])
						break
					}
				}
					
				if (numEvents == numEventsSent) {
					// If the number of events sent equals the total number of events, give a success message			
					log.info("Parsed test script and sent ${numEvents} events")
					flash.message = message(code: "osler.mb.tester.run.success.message", args: [numEvents])			
				} else if (numEventsSent > 0) {
					// If some, but not all, events were sent, it is a serious problem, so warn the user
					log.warn("Only ${numEventsSent} out of ${numEvents} were sent to the broker")
					flash.errors << message(code: "osler.mb.tester.run.incomplete.message", args:[numEventsSent, numEvents])			
				} else {
					// No events were sent, but no need to do anything
				}	
				redirect(view:"index")
				return				
				
			case MODE_MANUAL:
				log.debug("Going to manual screen with ${events.size()} events")
				render (view: "manual", model: [eventList: events])
				return 
				
			default:
				log.error("Unknown testing mode: ${params.mode}")
				flash.errors << "Unknown testing mode: ${params.mode}"
				redirect(view:"index")
				return
		}
	}
	
	/**
	 * Sends an event to Message Broker. Usually invoked asynchronously to manually
	 * send individual events.
	 */
	def send() {
		def errors = []		
		if (!params.method || params.method.equals("") || !params.body || params.body.equals("")) {
			errors << "Tried to send event but parameters were missing"
			log.error("Error in Ajax manual send - missing or malformed parameters: method='${params.method}', body='${params.body}'")
		} else {		
			// No initial errors, so send the event to Message Broker
			try {
				Integer responseCode = this.sendMessage(params.method, params.body)
				if (responseCode != 200) {
					// If SOAP call failed, add an error and report it back to the client
					errors << "Call failed with error code ${responseCode}"
					log.error("Failed to send SOAP message for event ${params.method} with error response code ${responseCode}")
				}
			} catch (Exception e) {
				errors << "An error occurred when sending the message: ${e.getMessage()}"
				log.error("send: Exception while sending the message: ${e.getMessage()}")
			}
		}			
		if (errors) {
			render (text: "<ul>" + errors.collect { "<li>" + it + "</li>" } + "</ul>", status: 501)			
		} else {
			render (text:"<span class='sent'>Sent</span>", status: 200)
		}
	}
	
	def locationTest() {
		[eventName: params.eventName ? params.eventName : "patientInCCU",
		 personType: params.personType ? params.personType : "patientId",
		 personId:params.personId ? params.personId : "Pa123456",
		 locationId: params.locationId ? params.locationId : "CCU12"]
	}
	
	def runLocationTest() {
		// Check the parameters to make sure we don't input something bad
		if (!params.eventName || !params.locationId || params.eventName.contains(" ") || !params.personId) { 
			flash.error = "Bad parameters. All are required."
			redirect(action: "locationTest")
			return
		}
		
		// Assemble the XML to send to message broker
		String dateFormat = grailsApplication.config.osler.mb.dateFormat	
		String dateValue = new Date().format(dateFormat)	
		def bodyWriter = new StringWriter()
		def xml = new groovy.xml.MarkupBuilder(bodyWriter)		
		xml."${params.eventName}" (sourceSuffix: "RTLS") {
			"${params.personType}"(params.personId)
			locationId(params.locationId)
			timestamp (dateValue)
		}	
		String body = bodyWriter.toString()			
		Integer responseStatusCode = this.sendMessage(params.eventName, body)
		
		// Report back to the user
		if (responseStatusCode < 400) {		
			log.info("${params.eventName} sent using the location tester")
			flash.message = "'${params.eventName}' was successfully sent to Message Broker. (${dateValue})"
		} else {
			log.error("${params.eventName} failed to send with location tester, returning status ${responseStatusCode}")
			flash.error = "'${params.eventName}' was not sent to the Message Broker due to communications issues. Response code was ${responseStatusCode}. (${dateValue})"
		}
		redirect(action:"locationTest", model: [eventName: params.eventName,
											personType: params.personType,
											personId: params.personId,
											locationId: params.locationId])
	}
	
	/**
	 * Takes a byte stream of XML and converts it into a list of event objects with the format {method, body}
	 * @param fileByteStream A byte stream coming from an XML file
	 * @param overrideTimestamps If true, replaces any timestamps' date with the current date.
	 */
	private List parseEvents(ByteArrayInputStream fileByteStream, boolean overrideTimestamps) {
		def results = []		
		def oslerTestScript
		String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss"
		SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat) // Used to parse string dates
		Date now = new Date() // Used to generate current date
	
		try {
			oslerTestScript = new XmlSlurper(false, false).parseText(fileByteStream.getText())
		} catch (SAXException saxe) {
			log.warn("User uploaded test script '${f.getName()}' (${f.getSize()} bytes) with XML errors: ${saxe.getMessage()}")
			flash.errors << message(code: "osler.mb.tester.run.xmlErrors.message")
			return
		}
		
		// Grab the name of the test for logging and messaging
		String testName = oslerTestScript.testName
		
		// Loop through each event
		for (def it : oslerTestScript.event) {
			// Pick the event name out of the event tag's name attribute
			String eventName = it.@name.text()
			// Pick the attribute sourceSuffix which will be used later
			String sourceSuffix = (!it.@sourceSuffix.text().equals("")) ? ' sourceSuffix="' + it.@sourceSuffix.text() + '"' : ""
			if ((!sourceSuffix.equals("")) && log.isDebugEnabled()) { log.debug("For ${eventName}, found source suffix: '${it.@sourceSuffix.text()}'") }
			
			// If overriding timestamps, change the date portion of the timestamp to today's date
			String ts = this.cleanTime(it.timestamp.text())
			try {
				Date d = sdf.parse(ts)
				if (overrideTimestamps) {
					d.year = now.year
					d.month = now.month
					d.date = now.date
					ts = d.format(timestampFormat)					
				}
			} catch (java.text.ParseException pe) {
				log.warn("Timestamp in test '${testName}' for event '${eventName}' contained the malformed timestamp ${ts}")
				flash.errors << "Timestamp for event '${eventName}' contained the malformed timestamp ${ts}. Overrode with current date and time."
				ts = now.format(timestampFormat) // Just use the old timestamp instead
			}
			it.timestamp = ts
						
			// Gather the text for the children nodes
			String childrenNodes = it.children().collect { "<${it.name()}>${it}</${it.name()}>" }.join('')
			// Then, create the entire event tag using the event's name, which is the format that MB wants
			// Add the attribute @sourceSuffix, which will be used by message broker for simulating virtual sources
			String bodyText = "<${eventName}${sourceSuffix}>${childrenNodes}</${eventName}>"
			
			results << [method: eventName, body: bodyText]
		}
		return results
	}
	
	/**
	 * Sends a message to message broker, using the configured access method,
	 * which is normally SOAP but could be REST or MQ or others
	 * @param method The method to call (used for SOAP mainly)
	 * @param body The payload to deliver in XML
	 * @return The response code from the server, 200 if success
	 * TODO Replace this with an interface and classloader construct
	 */
	private Integer sendMessage(String method, String body) {
		switch (grailsApplication.config.osler.mb.registerEventMethod) {
			case "SOAP":
				return this.sendSoap(method, body)
			case "HTTP":
			case "MQ":		
			default:
				return this.saveDirect(method, body)
		}
	}
	
	/**
	 * Sends an XML message via SOAP. The Message Broker host name is taken from the application configuration.
	 * @param soapMethod The name of the method that appears in the SOAP header
	 * @param soapBody The SOAP payload
	 * @return The HTTP response code. 200 if successful, otherwise 400 or 500.
	 * Note: Message Broker is very picky about the SOAP namespace - anything else is an error
	 */
	private Integer sendSoap(String soapMethod, String soapBody) {
		
		// Message broker and destinations are super picky about the SOAP namespace
		def soapNamespace = grailsApplication.config.osler.mb.soapNamespace
		def bodyNamespace = grailsApplication.config.osler.mb.eventNamespace
		soapBody = soapBody.replace("<${soapMethod}", "<ns1:${soapMethod}").replace("</${soapMethod}>", "</ns1:${soapMethod}>")		
		// Generate the SOAP message
		def soapRequest = """<soap:Envelope xmlns:soap="${soapNamespace}" xmlns:ns1="${bodyNamespace}"><soap:Header/><soap:Body>${soapBody}</soap:Body></soap:Envelope>"""
		String url = grailsApplication.config.osler.mb.registerEventUrls["SOAP"]
		if (log.isDebugEnabled()) { log.debug("Registering event ${soapMethod} via SOAP using '${url}': ${soapRequest}") }
		
		def soapUrl = new URL(url)

		// Connect to the host and send the message
		def connection = soapUrl.openConnection()
		connection.setRequestMethod("POST" )
		connection.setRequestProperty("Content-Length", String.valueOf( soapRequest.size() ) );
		connection.setRequestProperty("Content-Type" , "application/xml" )
		connection.doOutput = true
		
		// Write to stream
		try {
			Writer writer = new OutputStreamWriter(connection.outputStream)
			writer.write(soapRequest)
			writer.flush()
			writer.close()
			connection.connect()
			return connection.getResponseCode()
		} catch (java.net.ConnectException e) {
			log.error("Communication error sending SOAP message to message broker: ${e.getMessage()}")
			flash.error = "Failed to send message. Message Broker seems to be down."
			return 400		
		}
	}	
	
	/**
	 * Used in development modes when connection to Message Broker is down or unreliable.
	 * Directly saves the log into the database, bypassing the broker.
	 * @param body
	 * @param method
	 * @return Usually 200, unless save fails
	 */
	private Integer saveDirect(String method, String body) {
		log.debug("Method saveDirect in non-production mode - returning 200 OK")
		// Check the body if it has the sourceSuffix attribute and if it does, extract it
		String source = "TEST" + this.extractSuffix(body)
		// Create a log entry instead of sending it by SOAP to simulate it being logged by the broker
		try {
			def log = new Log(logTime: new Date(), event: method, source: source, inputMethod: "TEST-FAKE")
			log.save(failOnError: true)
			def responseLog = new ResponseLog(logTime: new Date(), event: method, destinationName: "TEST", 
				accessMethod: "DIRECT", 
				responseStatusCode: ((body.size() % 2) + 1) * 300 - 100 // Simulate 200s and 500s
				)
			responseLog.save(failOnError: true)
			return 200
		} catch (Exception e) {
			flash.errors << "Cannot save log entry due to validation errors"
			log.error("Could not save log in development mode: ${e.getMessage()}")
			return 500
		}
	}
	
	/**
	 * Cleans up a timestamp string, removing some known issues, such as a non-T divider or a trailing Z.
	 * @param ts A date/time in string format
	 */
	private String cleanTime(String ts) {
		ts = ts.trim()
		if (ts.charAt(10) != "T") {
			ts = ts.substring(0,10) + "T" + ts.substring(11, ts.length())
			log.debug("Timestamp passed in without dividing T")
		}		
		if (ts.endsWith("Z")) {
			ts = ts.substring(0, ts.length() - 1)
			log.debug("Timestamp passed in with trailing Z")
		}
		return ts
	}
	
	/**
	 * Extracts the value of the sourceSuffix attribute from a string. Used only
	 * in development mode as a convenience to ensure the log entries get correctly
	 * formed. Would be easier to do with XML parsing but not worth the trouble.
	 * @param x The XML string blob
	 * @return If the XML contains the attribute, returns the "-@sourceSuffix", otherwise ""
	 */
	private String extractSuffix(String x) {
		String s = "sourceSuffix="		
		if (x.contains(s+"'") || x.contains(s+'"')) {
			String q = x.charAt(x.indexOf(s) + s.length())
			return "-" + x.substring(x.indexOf(s) + s.length()+1, x.indexOf(q, x.indexOf(s) + s.length()+1))
		} else {
			return ""
		}		
	}
	
}
