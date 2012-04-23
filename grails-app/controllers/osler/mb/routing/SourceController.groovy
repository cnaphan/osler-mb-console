package osler.mb.routing

import org.springframework.dao.DataIntegrityViolationException
import osler.mb.xml.*;

class SourceController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
		def trans = XmlTransport.getInstance()
		def rr = trans.getRoutingRules()
		return trans.listSources(rr, params)
    }

    def create() {		
        [sourceInstance: new osler.mb.routing.Source()]
    }

    def save() {
        def sourceInstance = new osler.mb.routing.Source(params)
		
		if (!sourceInstance.validate()) {
            render(view: "create", model: [sourceInstance: sourceInstance])
            return
		}
		
		def trans = XmlTransport.getInstance()
		def rr = trans.getRoutingRules()
		
		// Check if any event with this name exists, if so give an error
		if (rr.sources.source.findAll{ it == sourceInstance.name }.size() > 0) {
			sourceInstance.errors.reject("osler.mb.routing.Source.not.unique", sourceInstance.name)
			render(view: "create", model: [sourceInstance: sourceInstance])
			return
		}
		
		// Add the event to the file
		rr.sources.appendNode { 
			source {				
				name(sourceInstance.name)
				accessMethod(sourceInstance.accessMethod)
				matchingString(sourceInstance.matchingString)	
			}
		}
		
		// Send the file back
		if (!trans.updateRoutingRules(rr)) {
			sourceInstance.errors.reject("osler.mb.routing.Source.failedToUpdate")			
            render(view: "create", model: [sourceInstance: sourceInstance])
            return
		}
		
		log.info("Created source ${sourceInstance.name}")
		flash.message = message(code: 'default.created.message', args: [message(code: 'osler.mb.routing.Source.label', default: 'Source'), sourceInstance.name])
		redirect(action: "show", id: sourceInstance.name)
    }

    def show() {
        def trans = XmlTransport.getInstance()
		def rr = trans.getRoutingRules()
		def sourceInstance = trans.getSourceByName(rr, params)
        if (!sourceInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'osler.mb.routing.Source.label', default: 'Source'), params.id])
            redirect(action: "list")
            return
        }

        [sourceInstance: sourceInstance]
    }

    def edit() {
		def trans = XmlTransport.getInstance()
		def rr = trans.getRoutingRules()
        def sourceInstance = trans.getSourceByName(rr, params)
        if (!sourceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'osler.mb.routing.Source.label', default: 'Source'), params.id])
            redirect(action: "list")
            return
        }

        [sourceInstance: sourceInstance]
    }

	def update() {
		def trans = XmlTransport.getInstance()
		def rr = trans.getRoutingRules()
        def sourceInstance = trans.getSourceByName(rr, params)
		
        if (!sourceInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'osler.mb.routing.Source.label', default: 'Source'), params.id])
            redirect(action: "list")
            return
        }
		sourceInstance.applyProperties(params)		
        
        if (!sourceInstance.validate()) {
            render(view: "edit", model: [sourceInstance: sourceInstance])
            return
        }

		def existingSource = rr.sources.source.find { it.name == params.id }
		existingSource.name.replaceNode{ name(sourceInstance.name) }
		existingSource.accessMethod.replaceNode{ accessMethod(sourceInstance.accessMethod) }
		existingSource.matchingString.replaceNode{ matchingString(sourceInstance.matchingString) }
		
		if (!trans.updateRoutingRules(rr)) {
			sourceInstance.errors.reject("osler.mb.routing.event.failedToUpdate")
			render(view: "edit", model: [sourceInstance: sourceInstance])
			return
		}
		
		log.info("Updated source ${sourceInstance.name}")		
		flash.message = message(code: 'default.updated.message', args: [message(code: 'osler.mb.routing.Source.label', default: 'Source'), sourceInstance.name])
        redirect(action: "show", id: sourceInstance.name)
    }

    def delete() {		
		def trans = XmlTransport.getInstance()
		def rr = trans.getRoutingRules()
        def sourceInstance = trans.getSourceByName(rr, params)
		
        if (!sourceInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'osler.mb.routing.Source.label', default: 'Source'), params.id])
            redirect(action: "list")
            return
        }
		
		rr.sources.source.find { it.name == params.id }.replaceNode {}
		
		if (!trans.updateRoutingRules(rr)) {
			sourceInstance.errors.reject("osler.mb.routing.Source.failedToUpdate")
			render(view: "show", model: [sourceInstance: sourceInstance])
			return
		}
		log.info("Deleted source ${sourceInstance.name}")
		flash.message = message(code: 'default.deleted.message', args: [message(code: 'osler.mb.routing.Source.label', default: 'Source'), params.id])
        redirect(action: "list")       
    }
}
