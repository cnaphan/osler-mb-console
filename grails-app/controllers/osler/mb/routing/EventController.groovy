package osler.mb.routing

import org.springframework.dao.DataIntegrityViolationException

class EventController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", updaterouting: "POST", getDefaultRoutingRules: "GET", receiveEventSoap: "POST", receiveEventHttp: "POST" ]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {		
		def trans = XmlTransport.getInstance()
		def rr
		try {
			rr = trans.getRoutingRules()
		} catch (java.net.ConnectException e) {
			flash.error = "There was a problem communicating with Message Broker"
			log.error("Failed to get routing rules: ${e.getMessage()}")
			redirect(uri: "/")
			return
		}
		return trans.listEvents(rr, params)
    }

    def create() {
		def e = new Event()
        [eventInstance: e]
    }

	def routing() {
		def trans = XmlTransport.getInstance()
		def rr
		try {
			rr = trans.getRoutingRules()
		} catch (java.net.ConnectException e) {
			flash.error = "There was a problem communicating with Message Broker"
			log.error("Failed to get routing rules: ${e.getMessage()}")
			redirect(uri: "/")
			return
		}
		def eventList = trans.listEvents(rr, [sort: "name", order: "asc", max: -1]).eventInstanceList
		def destinationList = trans.listDestinations(rr, [sort: "name", order: "asc", max: -1]).destinationInstanceList
		log.info("Loading routing rules with ${eventList.size()} events and ${destinationList.size()} destinations")

		[eventInstanceList: eventList, destinationInstanceList: destinationList]
	}
	
	/**
	 * Updates the routing rules using the changes made in the form. Typically, sends the results back to Message Broker.
	 */
	def updaterouting() {
		if (log.isDebugEnabled()) { log.debug("updaterouting: Received ${params.paths.toString()}") }
		def trans = XmlTransport.getInstance()
		def rr
		try {
			rr = trans.getRoutingRules()
		} catch (java.net.ConnectException e) {
			flash.error = "There was a problem communicating with Message Broker"
			log.error("Failed to get routing rules: ${e.getMessage()}")
			redirect(uri: "/")
			return
		}
		def destinations = trans.listDestinations(rr, [sort: "name", order: "asc", max: -1]).destinationInstanceList
		def events = trans.listEvents(rr, [sort: "name", order: "asc", max: -1]).eventInstanceList

		int changesMade = 0
		destinations.each { Destination d ->
			def existingDestination = rr.destinations.destination.find { it.name == d.name}
			// Go through each destination
			events.each { Event e ->
				String key = "${d.name}.${e.name}"								
				// And go through each event				
				if (params.paths.containsKey(key)) {
					// If the paths parameter contains an entry with the key "destinationId.eventId"
					if (!d.events.contains(e.name)) {
						// And the event does NOT contain the destination, add it						
						existingDestination.receives.appendNode { event(e.name) }
						changesMade++					
					}	
				} else {
					// If the paths parameter does not contain the entry (it was unchecked)
					if (d.events.contains(e.name)) {
						// And the event had this destination, remove it from the event's destinations
						def existingEvent = existingDestination.receives.event.find { it == e.name }
						existingEvent.replaceNode{}
						changesMade++
					}
				}
			}
		}
		if (changesMade > 0) {
			if (!trans.updateRoutingRules(rr)) {
				flash.error = "Failed to update routing rules"
				log.error("Update routing rules failed")
			}
		}		
		log.info("updaterouting: Routing paths updated with ${changesMade} changes")
		flash.message = message(code:"osler.mb.routing.EventRouting.updated.message", args: [changesMade])
		redirect(action: "routing")
	}
	
    def save() {
        def eventInstance = new Event()
		eventInstance.name = params.name
		
		if (!eventInstance.validate()) {
            render(view: "create", model: [eventInstance: eventInstance])
            return
		}
		
		def trans = XmlTransport.getInstance()
		def rr = trans.getRoutingRules()
		
		// Check if any event with this name exists, if so give an error
		if (rr.events.event.findAll{ it == eventInstance.name }.size() > 0) {
			eventInstance.errors.reject("osler.mb.routing.event.not.unique", eventInstance.name)
			render(view: "create", model: [eventInstance: eventInstance])
			return
		}
		
		// Add the event to the file
		rr.events.appendNode { event(eventInstance.name) }
		
		// Send the file back
		if (!trans.updateRoutingRules(rr)) {
			eventInstance.errors.reject("osler.mb.routing.event.failedToUpdate")			
            render(view: "create", model: [eventInstance: eventInstance])
            return
		}
		
		flash.message = message(code: 'default.created.message', args: [message(code: 'event.label', default: 'Event'), eventInstance.name])
		redirect(action: "show", id: eventInstance.name)
		
    }

    def show() {
		def trans = XmlTransport.getInstance()
		def rr
		try {
			rr = trans.getRoutingRules()
		} catch (java.net.ConnectException e) {
			flash.error = "There was a problem communicating with Message Broker"
			log.error("Failed to get routing rules: ${e.getMessage()}")
			redirect(uri: "/")
			return
		}
		def eventInstance = trans.getEventByName(rr, params)
        if (!eventInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
            return
        }

        [eventInstance: eventInstance]
    }

    def edit() {
		def trans = XmlTransport.getInstance()
		def rr
		try {
			rr = trans.getRoutingRules()
		} catch (java.net.ConnectException e) {
			flash.error = "There was a problem communicating with Message Broker"
			log.error("Failed to get routing rules: ${e.getMessage()}")
			redirect(uri: "/")
			return
		}
        def eventInstance = trans.getEventByName(rr, params)
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
            return
        }

        [eventInstance: eventInstance]
    }

    def update() {
		def trans = XmlTransport.getInstance()
		def rr
		try {
			rr = trans.getRoutingRules()
		} catch (java.net.ConnectException e) {
			flash.error = "There was a problem communicating with Message Broker"
			log.error("Failed to get routing rules: ${e.getMessage()}")
			redirect(uri: "/")
			return
		}
        def eventInstance = trans.getEventByName(rr, params)
		
        if (!eventInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
            return
        }

        eventInstance.applyProperties(params)

        if (!eventInstance.validate()) {
            render(view: "edit", model: [eventInstance: eventInstance])
            return
        }

		rr.events.event.find { it == params.id }.replaceNode{ event(eventInstance.name)}
		
		if (!trans.updateRoutingRules(rr)) {
			eventInstance.errors.reject("osler.mb.routing.event.failedToUpdate")
			render(view: "edit", model: [eventInstance: eventInstance])
			return
		}
		
		flash.message = message(code: 'default.updated.message', args: [message(code: 'event.label', default: 'Event'), eventInstance.name])
        redirect(action: "show", id: eventInstance.name)
    }

    def delete() {		
		def trans = XmlTransport.getInstance()
		def rr
		try {
			rr = trans.getRoutingRules()
		} catch (java.net.ConnectException e) {
			flash.error = "There was a problem communicating with Message Broker"
			log.error("Failed to get routing rules: ${e.getMessage()}")
			redirect(uri: "/")
			return
		}
        def eventInstance = trans.getEventByName(rr, params)
		
        if (!eventInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'event.label', default: 'Event'), params.id])
            redirect(action: "list")
            return
        }
		
		if (eventInstance.destinations.size() > 0) {
			log.info("Attempted to delete ${eventInstance.toString()} while it has destinations")
			eventInstance.errors.reject("osler.mb.routing.Event.delete.withDestinations.message")
			render(view: "show", model: [eventInstance: eventInstance])
			return
		}

		rr.events.event.find { it == params.id }.replaceNode {}
		
		if (!trans.updateRoutingRules(rr)) {
			eventInstance.errors.reject("osler.mb.routing.event.failedToUpdate")
			render(view: "show", model: [eventInstance: eventInstance])
			return
		}
			
		flash.message = message(code: 'default.deleted.message', args: [message(code: 'event.label', default: 'Event'), params.id])
        redirect(action: "list")       
    }
	
	/**
	 * This method is invoked by the Message Broker via an HTTP GET request to obtain a default 
	 * routing rules table, in the event that the Message Broker does not have one (when MB is 
	 * first initialized or if the file is ever deleted)
	 * @return A routing rules file from the WEB-INF folder in XML format
	 */
	def getDefaultRoutingRules() {
		log.info("Default routing rules were requested from ${request.getRemoteHost()}")
		try {		
			def trans = new LocalFileXmlTransport()
			def f = new File(trans.getLocalFilePath())		
			render (text: f.getText(), contentType: "text/xml", encoding: "UTF-8", status: 200)
		} catch (Exception e) {
			log.error("Attempted to retrieve default routing rules but failed with message: ${e.getMessage()}")
			render (text: "Problem retrieving default routing rules: ${e.getMessage()}", status: 500)
		}
	}
	
	/**
	 * Receives an event and writes to the logger. Used to test the message flow by having the console act as a destination.
	 */
	def receiveEventSoap () {
		try {
			def xml = request.XML
			log.info("EVENT HANDLER - Event received via SOAP from ${request.getRemoteHost()}:\n\tContents: ${xml.Body.children()[0]}")
			render(status: 200) // Respond with 200 Ack
		} catch (Exception e) {
			log.error("Failed in SOAP event handler: ${e.getMessage()}")
			render (text:  e.getMessage(), status: 500) // Respond with 500 server error
		}
	}	
   def receiveEventHttp () {
	   try {
		   def xml = request.XML
		   log.info("EVENT HANDLER - Event received via HTTP from ${request.getRemoteHost()}:\n\tContents: ${xml}")
		   render(status: 200) // Respond with 200 Ack
	   } catch (Exception e) {
		   log.error("Failed in HTTP event handler: ${e.getMessage()}")
		   render (text:  e.getMessage(), status: 500) // Respond with 500 server error
	   }
   }

	
		
}
