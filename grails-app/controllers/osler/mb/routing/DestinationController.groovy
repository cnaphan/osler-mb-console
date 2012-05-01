package osler.mb.routing

import org.springframework.dao.DataIntegrityViolationException
import osler.mb.xml.*;

class DestinationController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

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
		return trans.listDestinations(rr, params)
    }

    def create() {		
        [destinationInstance: new osler.mb.routing.Destination()]
    }

    def save() {
        def destinationInstance = new osler.mb.routing.Destination(params)
		
		if (!destinationInstance.validate()) {
            render(view: "create", model: [destinationInstance: destinationInstance])
            return
		}
		
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
		
		// Check if any event with this name exists, if so give an error
		if (rr.destinations.destination.findAll{ it == destinationInstance.name }.size() > 0) {
			destinationInstance.errors.reject("osler.mb.routing.Destination.not.unique", destinationInstance.name)
			render(view: "create", model: [destinationInstance: destinationInstance])
			return
		}
		
		// Add the event to the file
		rr.destinations.appendNode { 
			destination () {
				disabled(destinationInstance.disabled)				
				name(destinationInstance.name)
				description(destinationInstance.description)
				url(destinationInstance.url)
				accessMethod(destinationInstance.accessMethod)
				receives{}	
			}
		}
		
		// Send the file back
		if (!trans.updateRoutingRules(rr)) {
			destinationInstance.errors.reject("osler.mb.routing.Destination.failedToUpdate")			
            render(view: "create", model: [destinationInstance: destinationInstance])
            return
		}
		
		flash.message = message(code: 'default.created.message', args: [message(code: 'osler.mb.routing.Destination.label', default: 'Destination'), destinationInstance.name])
		redirect(action: "show", id: destinationInstance.name)
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
		def destinationInstance = trans.getDestinationByName(rr, params)		
        if (!destinationInstance) {
			flash.error = message(code: 'default.not.found.message', args: [message(code: 'osler.mb.routing.Destination.label', default: 'Destination'), params.id])
            redirect(action: "list")
            return
        }

        [destinationInstance: destinationInstance]
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
        def destinationInstance = trans.getDestinationByName(rr, params)
        if (!destinationInstance) {
            flash.error = message(code: 'default.not.found.message', args: [message(code: 'osler.mb.routing.Destination.label', default: 'Destination'), params.id])
            redirect(action: "list")
            return
        }

        [destinationInstance: destinationInstance]
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
        def destinationInstance = trans.getDestinationByName(rr, params)		
		
        if (!destinationInstance) {
            flash.error = message(code: 'default.not.found.message', args: [message(code: 'osler.mb.routing.Destination.label', default: 'Destination'), params.id])
            redirect(action: "list")
            return
        }
		destinationInstance.applyProperties(params)		
        
        if (!destinationInstance.validate()) {
            render(view: "edit", model: [destinationInstance: destinationInstance])
            return
        }

		def existingDestination = rr.destinations.destination.find { it.name == params.id }
		existingDestination.disabled = destinationInstance.disabled
		existingDestination.name.replaceNode{ name(destinationInstance.name) }
		existingDestination.description.replaceNode{ description(destinationInstance.description) }
		existingDestination.url.replaceNode{ url(destinationInstance.url) }
		existingDestination.accessMethod.replaceNode{ accessMethod(destinationInstance.accessMethod) }
		
		if (!trans.updateRoutingRules(rr)) {
			destinationInstance.errors.reject("osler.mb.routing.event.failedToUpdate")
			render(view: "edit", model: [destinationInstance: destinationInstance])
			return
		}
		
		flash.message = message(code: 'default.updated.message', args: [message(code: 'osler.mb.routing.Destination.label', default: 'Destination'), destinationInstance.name])
        redirect(action: "show", id: destinationInstance.name)
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
        def destinationInstance = trans.getDestinationByName(rr, params)
		
        if (!destinationInstance) {
			flash.error = message(code: 'default.not.found.message', args: [message(code: 'osler.mb.routing.Destination.label', default: 'Destination'), params.id])
            redirect(action: "list")
            return
        }
		
		if (destinationInstance.events.size() > 0) {
			log.info("Attempted to delete ${destinationInstance.toString()} while it receives events")
			destinationInstance.errors.reject("osler.mb.routing.Destination.delete.withEvents.message")
			render(view: "show", model: [destinationInstance: destinationInstance])
			return
		}

		rr.destinations.destination.find { it.name == params.id }.replaceNode {}
		
		if (!trans.updateRoutingRules(rr)) {
			destinationInstance.errors.reject("osler.mb.routing.Destination.failedToUpdate")
			render(view: "show", model: [destinationInstance: destinationInstance])
			return
		}
			
		flash.message = message(code: 'default.deleted.message', args: [message(code: 'osler.mb.routing.Destination.label', default: 'Destination'), params.id])
        redirect(action: "list")       
    }
}
