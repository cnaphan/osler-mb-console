package osler.mb

import org.xml.sax.SAXException
import osler.mb.routing.*
import java.text.SimpleDateFormat
import java.util.*;

class TesterController {
	
	static allowedMethods = [index: "GET", run: "POST", send: "POST"]
	
	private static final String MODE_MANUAL = 1
	private static final String MODE_AUTO = 2
	private static final Map modeList = [(MODE_MANUAL):"osler.mb.tester.mode.manual.label",
										 (MODE_AUTO):"osler.mb.tester.mode.auto.label"]
	private static final String NS = "osler" // The namespace prefix used in outgoing messages

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
		List events = this.parseEvents(inStream, overrideTimestamps, false)
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
						
						if (log.isDebugEnabled()) { log.debug("Going to sleep for 1 second after sending event ${it.method}") }
						Thread.sleep(1000) // Delay for 1 second before sending the next event
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
	
	def rtlsSimulator() {
		[people: [
			[id: "Patient_ID", name: "Patient", value: "Pa123456", icon: "patient_48.png"], 
			[id: "Physician_ID", name: "Physician", value: "Phy777777", icon: "doctor_48.png"]
			//[id: "Provider_ID", name: "Provider", value: "Pro666666", icon: "nurse_female_dark_48.png"]
			], 
		 locations: [
		 	[id: "ED", value: "Bed207"],
		 	[id: "CCU", value: "Assessment12"]		 	
		 	]
		 ]
	}
	
	def runRtlsEvent() {
		this.sendRtlsEvent(params)		
		redirect(action:"rtlsSimulator")
	}
	
	
	def integrationTest () {	
		[xmlResources: ["small-integration-test-script.xml"]]
	}
	
	def runIntegrationTest () {
		log.info("Running integration test")
		def whatHappened = this.runIntegrationTestModule()
		assert whatHappened
		String color = "#B3FF99"
		String resultText = "Test succeeded"
		if (whatHappened.find { it.type?.equals("error") }) { 
			color = "#FF5353"
			resultText = "Test failed. Check the details for more information."
		} else if (whatHappened.find { it.type?.equals("warn") }) { 			
			color = "#FFF06A"
			resultText = resultText + " but with warnings. Check the details for more information."
		}

		[whatHappened: whatHappened, displayColor: color, resultText: resultText]
	}
	
	/**
	 * Executes the integration test. This method is easier to write as a method because it's easier to return.
	 */
	private List runIntegrationTestModule() {
		def w = []
		w << [type:"info", text:"Initiating integration test"]
		
		// Check some configuration elements before proceeding
		if (grailsApplication.config.osler.mb.registerEventMethod != "SOAP") {	w << [type:"warn", text:"The method to register events must be SOAP. Console is incorrectly configured (possibly in development mode)."] }
		if (w.find { it.type?.equals("error") }) { return w }
		
		// Check the test script for problems
		def f = request.getFile("testScript")					
		if (!f || f.isEmpty()) {
			w << [type:"error", text:"Test script was empty."]
			return w
		}		
		ByteArrayInputStream inStream = f.getInputStream()		
		def events = this.parseEvents(inStream, false, true) // Parse events in test mode
		if (!events) {
			w << [type:"error", text:"Test script did not contain any valid events."]
			return w
		} else {
			w << [type:"info", text:"Retrieved ${events.size()} events from test script. Forced events into 'test' mode."]
		}
		
		def trans = XmlTransport.getInstance()
		def rr
		try {
			rr = trans.getRoutingRules()
			w << [type:"info", text:"Retrieved routing rules with ${rr.events.event.size()} events and ${rr.destinations.destination.size()} destinations"]
		} catch (java.net.ConnectException e) {
			w << [type:"error", text:"An error occurred while retrieving the routing rules: ${e.getMessage()}"]
			return w
		}
		
		// Confirm the destinations exist and they are properly configured
		def eventCount = rr.events.event.size()
		// Initialize a list of receivers. Uses any receiver that is named "Test*"
		def dests = []
		trans.listDestinations(rr, [:]).destinationInstanceList.each {
			if (it.name.toLowerCase().startsWith("test")) {
				dests << it.name
			}
		}
		dests.each { destName ->
			def testDest = rr.destinations.destination.find{ it.name.text().toUpperCase() == destName.toUpperCase() }
			if (testDest) {
				if (testDest.receives.event.size() != eventCount) { 
					w << [type:"warn", text:"The destination ${destName} receives ${testDest.receives.event.size()} of ${eventCount} events. The test destination should receive all events."]					
				} else {
					w << [type:"info", text:"Confirmed that ${destName} exists and receives all ${eventCount} events."]
				}
			
			} else {
				w << [type:"warn", text:"Routing rules did not contain a destination called '${destName}'. This could cause problems testing responses."]
			}
		}
		
		def beforeSending = new Date() // Get a current time before sending the events
		Integer logCount = Log.count()
		Integer responseLogCount = ResponseLog.count()
		Integer resultCount = DestinationResult.count()
		
		// Send the test events to the broker
		for (def it : events) {		
			Integer responseCode = 0
			try {
				responseCode = this.sendMessage(it.method, it.body)																					
			} catch (java.net.UnknownHostException uhe) {
				w << [type:"error", text:"Could not send test event to the broker: ${uhe.getMessage()}"]
				return w
			}			
			if (responseCode != 200) { // 200=OK
				w << [type:"error", text:"Received error code ${responseCode} from the broker for one of the events."]
				return w
			}
		}		
		w << [type:"info", text:"Events sent to broker with no problems."]

		// Listen for new logs and compare them with what they ought to be
		Integer logsExpected = events.size()
		Integer responseLogsExpected = events.size() * dests.size()		
		Integer resultsExpected = responseLogsExpected		
		Integer waitSeconds = 1
		Integer secondsWaited = 0
		Integer secondsToWait = 5		
		boolean logReceived = false
		Integer newLogCount = 0
		Integer newResponseLogCount = 0
		Integer newResultCount = 0
		w << [type:"info", text:"Listening for incoming messages for ${secondsToWait} seconds. Expecting ${logsExpected} incoming logs, ${responseLogsExpected} response logs and ${resultsExpected} destination results."]
		while (!logReceived && secondsWaited < secondsToWait) {			
			w << [type:"info", text:"Going to sleep for ${waitSeconds} second(s) to wait for broker response"]
			Thread.currentThread().sleep(waitSeconds*1000)
			secondsWaited = secondsWaited + waitSeconds
			// Wake up and check log counts
			newLogCount = Log.count()
			newResponseLogCount = ResponseLog.count()
			newResultCount = DestinationResult.count()			
			logReceived = (newLogCount >= (logCount + logsExpected)) && 
				(newResponseLogCount >= (responseLogCount + responseLogsExpected)) &&
				(newResultCount >= (resultCount + resultsExpected))
			w << [type:"info", text:"Received ${ newLogCount - logCount } incoming log messages, ${ newResponseLogCount - responseLogCount } response log messages and ${ newResultCount - resultCount } destination results"]
		}
		if (!logReceived) {
			w << [type:"error", text:"Did not receive all messages in the time alloted.<br/>Received ${newLogCount - logCount} of ${logsExpected} incoming log messages, ${ newResponseLogCount - responseLogCount } of ${responseLogsExpected} response log messages and ${ newResultCount - resultCount } of ${resultsExpected} destination results."]
		} else {
			w << [type:"info", text:"Received all log messages and destination results."]
		}				
		
		// Check logs for incoming messages
		def logs = Log.findAllByLogTimeGreaterThanEquals(beforeSending)
		if (!logs) { w << [type:"error", text: "Could not retrieve any new incoming logs."] }
		else if (logs.size() > logsExpected) { w << [type:"warn", text:"Found more incoming logs than expected. Expected ${logsExpected} but found ${logs.size()}. There could be other broker activity interfering with the test."] }
		else if (logs.size() < logsExpected) { w << [type:"warn", text:"Found fewer incoming logs than expected. Expected ${logsExpected} but found ${logs.size()}. The broker may be slower than the integration test allows for."] }
		if (logs) {
			for (def l : logs) {
				if (l.numSentP2P != dests.size()) {
					w << [type: "warn", text: "The event ${l.event} was sent to ${l.numSentP2P} P2P destinations but ${dests.size()} were expected."]
				}
				if (l.numSentPubSub != 0) {
					w << [type: "warn", text: "The event ${l.event} was published to ${l.numSentPubSub} subscription points but none were expected."]
				}
			}
		}
		
		// Check response logs for error codes
		def responseLogs = ResponseLog.findAllByLogTimeGreaterThanEquals(beforeSending)
		if (!responseLogs) { w << [type:"error", text: "Could not retrieve any new response logs."] }
		else if (responseLogs.size() > responseLogsExpected) { w << [type:"warn", text:"Found more response logs than expected. Expected ${responseLogsExpected} but found ${responseLogs.size()}. There could be other broker activity interfering with the test."] }
		else if (responseLogs.size() < responseLogsExpected) { w << [type:"warn", text:"Found fewer response logs than expected. Expected ${responseLogsExpected} but found ${responseLogs.size()}. The broker may be slower than the integration test allows for."] }
		
		if (responseLogs.find{ it.responseStatusCode >= 400 }) {
			for (def r : responseLogs) {
				if (r.responseStatusCode >= 400) {
					w << [type:"error", text: "The destination ${r.destinationName} responded to the event ${r.event} with status code ${r.responseStatusCode}"]
				}
			}
		} else if (responseLogs) {
			w << [type:"info", text:"${responseLogs.size()} response logs checked. The test destinations did not respond to message broker with errors."]
		}
		
		// Check destination results for errors
		def results = DestinationResult.findAllByLogTimeGreaterThanEquals(beforeSending)
		if (!results) { w << [type:"error", text: "Could not retrieve any new destination results."] }
		else if (results.size() > resultsExpected) { w << [type:"warn", text:"Found more results than expected. Expected ${resultsExpected} but found ${results.size()}. There could be other broker activity interfering with the test."] }
		else if (results.size() < resultsExpected) { w << [type:"warn", text:"Found fewer results than expected. Expected ${resultsExpected} but found ${results.size()}. The broker may be slower than the integration test allows for."] }

		if (results.find{ it.errorXml }) {
			for (def r : results) {
				if (r.errorXml) {
					def xml = new XmlSlurper(false, false).parseText(r.errorXml)
					def errorList = xml.entry?.collect { '<li>${it.@key} ${it.text()}</li>' }?.join("\n")
					w << [type:"error", text:"An error was detected in the result of event ${r.eventName} by method ${r.method}:\n<ul>${errorList}</ul>"]
				}
			}
		} else if (results) {
			w << [type:"info", text:"${results.size()} destination results checked. No errors reported in events received by console."]
		}
		return w
	}
	
	/**
	 * Takes a byte stream of XML and converts it into a list of event objects with the format {method, body}
	 * @param fileByteStream A byte stream coming from an XML file
	 * @param overrideTimestamps If true, replaces any timestamps' date with the current date.
	 */
	private List parseEvents(ByteArrayInputStream fileByteStream, boolean overrideTimestamps, boolean forceTest = false) {
		def results = []		
		def oslerTestScript
		String timestampFormat = grailsApplication.config.osler.mb.dateFormat
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
			// If we're forcing the events into test mode or if the test attribute is true, set the event into test mode
			String isTest = (forceTest || it.@test?.text()?.equals("true")) ? ' test="true"' : ''
			
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
			String childrenNodes = it.children().collect { "<${NS}:${it.name()}>${it}</${NS}:${it.name()}>" }.join('')
			// Then, create the entire event tag using the event's name, which is the format that MB wants
			// Add the attribute @sourceSuffix, which will be used by message broker for simulating virtual sources
			String bodyText = "<${NS}:${eventName}${sourceSuffix}${isTest}>${childrenNodes}</${NS}:${eventName}>"
			
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
	 * @param soapMethod The name of the method that appears in the HTTP header. It should be the event name.
	 * @param soapBody The SOAP payload
	 * @return The HTTP response code. 200 if successful, otherwise 400 or 500.
	 * Note: Message Broker is very picky about the SOAP namespace - anything else is an error
	 */
	private Integer sendSoap(String soapMethod, String soapBody) {
		
		// Message broker and destinations are super picky about the SOAP namespace
		def soapNamespace = grailsApplication.config.osler.mb.soapNamespace
		def bodyNamespace = grailsApplication.config.osler.mb.eventNamespace				
		// Generate the SOAP message
		def soapRequest = "<soapenv:Envelope xmlns:soapenv=\"${soapNamespace}\" xmlns:${NS}=\"${bodyNamespace}\"><soapenv:Header/><soapenv:Body>${soapBody}</soapenv:Body></soapenv:Envelope>"
		String url = grailsApplication.config.osler.mb.registerEventUrls["SOAP"]
		if (log.isDebugEnabled()) { log.debug("Registering event ${soapMethod} via SOAP using '${url}': ${soapRequest}") }		
		 
		def soapUrl = new URL(url)

		// Connect to the host and send the message
		def connection = soapUrl.openConnection()
		connection.setRequestMethod("POST" )
		connection.setRequestProperty("Content-Length", String.valueOf( soapRequest.size() ) );
		connection.setRequestProperty("Content-Type" , "application/xml" )
		connection.setRequestProperty("Accept" , "*/*" )
		connection.setRequestProperty("Host" , request["Host"] )
		// Required by some destinations for processing
		connection.setRequestProperty("SOAPAction" , "\"${bodyNamespace}/${soapMethod}\"")
		connection.doOutput = true
		
		// Write to stream
		try {
			Writer writer = new OutputStreamWriter(connection.outputStream)
			writer.write(soapRequest)
			writer.flush()
			writer.close()
			connection.connect()
			def code = connection.getResponseCode()
			if (code >= 400) {
				log.warn("Console failed to send event to broker at ${url}:\n${soapRequest}")
			}
			return code
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
	
	/**
	 * Sends an RTLS event, given the minimum necessary parameters. Used by ad hoc location tests and RTLS simulations.
	 * @return True if success, false if failure
	 */
	private boolean sendRtlsEvent(def params) {
		if (!params.eventName || !params.locationId || params.eventName.contains(" ") || !params.personId || !params.personType) { 
			flash.error = "Bad parameters. All are required."			
			return
		}
		// Assemble the XML to send to message broker
		String dateFormat = grailsApplication.config.osler.mb.dateFormat	
		String dateValue = new Date().format(dateFormat)	
/*		def bodyWriter = new StringWriter()
		def xml = new groovy.xml.MarkupBuilder(bodyWriter)		
		xml."${params.eventName}" (sourceSuffix: "RTLS") {
			"${params.personType}"(params.personId)
			Location_ID(params.locationId)
			timestamp (dateValue)
		}	
		String body = bodyWriter.toString()*/		
		String body = "<${NS}:${params.eventName} sourceSuffix='RTLS'><${NS}:${params.personType}>${params.personId}</${NS}:${params.personType}><${NS}:Location_ID>${params.locationId}</${NS}:Location_ID><${NS}:timestamp>${dateValue}</${NS}:timestamp></${NS}:${params.eventName}>"
		Integer responseStatusCode = this.sendMessage(params.eventName, body)
		
		// Report back to the user
		if (responseStatusCode < 400) {		
			log.debug("${params.eventName} sent using the location tester")
			flash.message = "'${params.eventName}' was successfully sent to Message Broker. (${dateValue})"
			return true
		} else {
			log.error("${params.eventName} failed to send with location tester, returning status ${responseStatusCode}")
			flash.error = "'${params.eventName}' was not sent to the Message Broker due to communications issues. Response code was ${responseStatusCode}. (${dateValue})"
			return false
		}
	}
	
}
