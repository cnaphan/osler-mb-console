package osler.mb

import grails.converters.XML

/**
 * Provides a series of actions for receiving events, as if the console were a destination itself. It can receive
 * in SOAP, HTTP or TWS format. TWS ("Teamwork Services" AKA Lombardi) is the special format that Lombardi uses.
 * It differs slightly from the normal format. If there are errors in the format, a machine and human-readable 
 * XML-ified map is returned with the errors. This is to facilitate using these methods in automated tests in 
 * the future.
 * 
 * The namespaces for the particular XML formats are drawn from the Grails configuration. If the namespaces changed,
 * they would have to be changed in the config, or else the receive actions will fail with 500.
 * 
 * @author cnaph035
 */
class ReceiveController {

	static allowedMethods = [soap: "POST", http: "POST", tws: "POST" ]

	/**
	 * Receives an event and writes to the logger. Used to test the message flow by having the console act as a destination.
	 * Returns 200 if the event is properly formatted. Returns 500 if errors are detected, plus errors in XML format, for automated tests.
	 */
	def soap () {
		try {
			def xml = request.XML
			def errors = [:]
			// Try to detect errors in the canonical SOAP format, determined by Shirley's WSDL
			if (this.testEquals(errors, "NoEnvelope", xml.name(), "Envelope")) {
				this.testEquals(errors, "BadEnvelopeNS", xml.namespaceURI(), grailsApplication.config.osler.mb.soapNamespace)
				if (this.testEquals(errors, "NoBody", xml.children()[-1].name(), "Body")) {
					def b = xml.children()[-1]
					this.testEquals(errors, "BadBodyNS", b.namespaceURI(), grailsApplication.config.osler.mb.soapNamespace)
					// Ensure the event's namespace is Shirley's
					if (this.testEquals(errors, "BadBodyChildrenNum", b.children().size(), 1)) {
						def e = b.children()[0]
						this.testEquals(errors, "BadEventNS", e.namespaceURI(), grailsApplication.config.osler.mb.eventNamespace)
						// Ensure all the children of the event have NO namespace
						e.children().each {
							this.testEquals(errors, "BadParameterNS-${it.name()}", it.namespaceURI(), "")
							this.testNotContains(errors, "ParameterWithUnderscore-${it.name()}", it.name(), "_")
							this.testEquals(errors, "ParameterWith1stUpperCase-${it.name()}", it.name()[0], it.name().toLowerCase()[0])
						}
						if (this.testEquals(errors, "LastParameterNotTimestamp", e.children()[-1].name(), "timestamp")) {
							this.testDateFormat(errors,"TimestampFormat", e.children()[-1].text())
						}
					}
				}
			}

			if (!errors) {
				log.info("SOAP event ${xml.Body.children()[0].name()} received from ${request.getRemoteHost()}")
				render(status: 200) // Respond with 200 Ack
			} else {
				log.warn("SOAP event received from ${request.getRemoteHost()} with the following errors: ${errors as XML}")
				render(text: errors as XML, status: 500) // Respond with 500 Internal Error
			}
		} catch (Exception e) {
			log.error("Failed in SOAP event handler: ${e.getMessage()}")
			render (text: e.getMessage(), status: 500) // Respond with 500 server error
		}
	}

	def http () {
		try {
			def xml = request.XML
			def errors = [:]

			// Try to detect errors in the canonical HTTP format
			this.testEquals(errors, "BadEventNS", xml.namespaceURI(), grailsApplication.config.osler.mb.eventNamespace)
			xml.children().each { this.testEquals(errors, "BadEvent${it.name()}NS", it.namespaceURI(), "") }
			if (this.testEquals(errors, "LastParameterNotTimestamp", e.children()[-1].name(), "timestamp")) {
				this.testDateFormat(errors,"TimestampFormat", e.children()[-1].text())
			}
			if (!errors) {
				log.info("HTTP event ${xml.name()} received from ${request.getRemoteHost()}")
				render(status: 200) // Respond with 200 Ack
			} else {
				log.warn("HTTP event received from ${request.getRemoteHost()} with the following errors: ${errors as XML}")
				render(text: errors as XML, status: 500) // Respond with 500 Internal Error
			}
		} catch (Exception e) {
			log.error("Failed in HTTP event handler: ${e.getMessage()}")
			render (text:  e.getMessage(), status: 500) // Respond with 500 server error
		}
	}

	def tws () {
		try {
			def xml = request.XML
			def errors = [:]
			// Try to detect errors in the TWS format, determined by Lombardi's WSDL
			if (this.testEquals(errors, "NoEnvelope", xml.name(), "Envelope")) {
				this.testEquals(errors, "BadEnvelopeNS", xml.namespaceURI(), grailsApplication.config.osler.mb.soapNamespace)
				this.testEquals(errors, "NoHeader", xml.children()[0].name(), "Header")
				this.testEquals(errors, "BadHeaderNS", xml.children()[0].namespaceURI(), grailsApplication.config.osler.mb.soapNamespace)
				if (this.testEquals(errors, "NoBody", xml.children()[1].name(), "Body")) {
					def b = xml.children()[1]
					this.testEquals(errors, "BadBodyNS", b.namespaceURI(), grailsApplication.config.osler.mb.soapNamespace)
					if (this.testEquals(errors, "BadBodyChildrenNum", b.children().size(), 1)) {
						def e = b.children()[0]
						this.testEquals(errors, "BadEventNS", e.namespaceURI(), grailsApplication.config.osler.mb.twsNamespace)
						this.testEquals(errors, "EventWith1stLowerCase}", e.name()[0], e.name().toUpperCase()[0])
						// Ensure all the children of the event have NO namespace
						e.children().each {
							this.testEquals(errors, "BadParameterNS-${it.name()}", it.namespaceURI(), grailsApplication.config.osler.mb.twsNamespace)
						}
						if (this.testEquals(errors, "LastParameterNotTimestamp", e.children()[-1].name(), "timestamp")) {
							this.testDateFormat(errors,"TimestampFormat", e.children()[-1].text())
						}
					}
				}
			}

			if (!errors) {
				log.info("TWS event ${xml.Body.children()[0].name()} received from ${request.getRemoteHost()}")
				render(status: 200) // Respond with 200 Ack
			} else {
				log.warn("TWS event received from ${request.getRemoteHost()} with the following errors: ${errors as XML}")
				render(text: errors as XML, status: 500) // Respond with 500 Internal Error
			}
		} catch (Exception e) {
			log.error("Failed in TWS event handler: ${e.getMessage()}")
			render (text: e.getMessage(), status: 500) // Respond with 500 server error
		}

	}

	private boolean testEquals (def errors, String errorKey, def actual, def expected) {
		if (actual != expected) {
			errors[errorKey] = "Expected ${expected} but was ${actual}"
			return false
		} else {
			return true
		}
	}

	private boolean testNull (def errors, String errorKey, def actual) {
		if (actual != null) {
			errors[errorKey] = "Expected null but was ${actual}"
			return false
		} else {
			return true
		}
	}

	private boolean testNotContains (def errors, String errorKey, String str, String substr) {
		if (str.contains(substr)) {
			errors[errorKey] = "Expected ${str} not to contain ${substr} but it did"
			return false
		} else {
			return true
		}
	}
	private boolean testTrue (def errors, String errorKey, boolean test) {
		if (test) {
			errors[errorKey] = "Expected false but was true"
			return false
		} else {
			return true
		}
	}
	private boolean testDateFormat(def errors, String errorKey, String dateString) {
		String timestampFormat = grailsApplication.config.osler.mb.dateFormat
		def sdf = new java.text.SimpleDateFormat(timestampFormat)
		try {
			sdf.parse(dateString)
			return true
		} catch (java.text.ParseException pe) {
			errors[errorKey] = "Expected ${dateString} to be in format '${grailsApplication.config.osler.mb.dateFormat}' but it wasn't"
			return false
		}
	}



}
