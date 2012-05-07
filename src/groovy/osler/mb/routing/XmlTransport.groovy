package osler.mb.routing

abstract class XmlTransport {
	
	public static XmlTransport getInstance() {
		def grailsApplication = new Log().domainClass.grailsApplication		
		switch(grailsApplication.config.osler.mb.routingRulesTransportMode) {
			case "REST":
				return new RestXmlTransport()
			default:				
				return new LocalFileXmlTransport()				
		}
	}
	
	public abstract groovy.util.slurpersupport.GPathResult getRoutingRules()

	public abstract boolean updateRoutingRules(def root)
	
	public osler.mb.routing.Event getEventByName (def rr, def params) {	
		def eventNames = rr.events.event.findAll { it == params.id }
		if (!eventNames || eventNames.size() == 0) {
			return null
		} else if (eventNames.size() > 1) {
			throw new Exception ("Found ${eventNames.size()} events in routing-rules file with name '${params.id}'")
		} else {
			def eventName = eventNames[0]
			def e = new osler.mb.routing.Event(name: eventName)
			rr.destinations.destination.each { d ->
				if (d.receives.event.findAll{ it == eventName }.size() > 0) {
					e.destinations << d.name
				}
			}
			return e
		}
	}
	
	public osler.mb.routing.Destination getDestinationByName (def rr, def params) {
		def destinations = rr.destinations.destination.findAll { it.name == params.id }
		if (destinations.size() == 0) {
			return null
		} else if (destinations.size() > 1) {
			throw new Exception ("Found ${destinations.size()} destinations in routing-rules file with name '${params.id}'")
		} else {
			def d = destinations[0]
			def destinationObject = new osler.mb.routing.Destination(name: d.name, description: d.description, url: d.url, accessMethod: d.accessMethod, format: d.format, disabled: d.disabled?.equals("true"))
			d.receives.event.each { e ->				
				destinationObject.events << e.name				
			}
			return destinationObject
		}
	}
	
	public osler.mb.routing.Source getSourceByName (def rr, def params) {
		def sources = rr.sources.source.findAll { it.name == params.id }
		if (sources.size() == 0) {
			return null
		} else if (sources.size() > 1) {
			throw new Exception ("Found ${sources.size()} sources in routing-rules file with name '${params.id}'")
		} else {
			def s = sources[0]			
			return new osler.mb.routing.Source(name: s.name.text(), accessMethod: s.accessMethod.text(), matchingString: s.matchingString.text())
		}
	}
	
	def listEvents(groovy.util.slurpersupport.GPathResult rr, def params) {
		if (!params.max) { params.max = 10 }
		if (!params.offset) { params.offset = 0 }		
		def eventInstanceList = new LinkedList<osler.mb.routing.Event>()
		for (def eventName: rr.events.event) {
			eventInstanceList << new osler.mb.routing.Event(name: eventName)
		}
		Integer eventInstanceTotal = eventInstanceList.size()
		if (params.sort) {
			eventInstanceList.sort {a,b -> a.compareTo(b) * (params.order?.equals("asc") ? 1 : -1) }
		}
		if ((params.max != -1) && (eventInstanceList.size() >= new Integer(params.max))) {
			eventInstanceList = eventInstanceList[new Integer(params.offset)..(Math.min(new Integer(params.offset)+new Integer(params.max), eventInstanceTotal)-1)]
		}		
		
		[eventInstanceList: eventInstanceList, eventInstanceTotal: eventInstanceTotal]
	}
	
	def listDestinations(groovy.util.slurpersupport.GPathResult rr, def params) {
		if (!params.max) { params.max = 10 }
		if (!params.offset) { params.offset = 0 }
		def destinationInstanceList = new LinkedList<osler.mb.routing.Destination>()
		for (def d: rr.destinations.destination) {
			def events =  d.receives.event.collect{ it.toString() }			
			destinationInstanceList << new osler.mb.routing.Destination(name: d.name, description: d.description, url: d.url, accessMethod: d.accessMethod, format: d.format, disabled: d.disabled?.equals("true"), events: events)
		}
		Integer destinationInstanceTotal = destinationInstanceList.size()
		if (params.sort) {
			destinationInstanceList.sort {a,b -> a.compareTo(b) * (params.order?.equals("asc") ? 1 : -1) }
		}
		if ((params.max != -1) && (destinationInstanceList.size() >= new Integer(params.max))) {
			destinationInstanceList = destinationInstanceList[new Integer(params.offset)..(Math.min(new Integer(params.offset)+new Integer(params.max), destinationInstanceTotal)-1)]
		}		
		[destinationInstanceList: destinationInstanceList, destinationInstanceTotal: destinationInstanceTotal]
	}
	
	def listSources(groovy.util.slurpersupport.GPathResult rr, def params) {
		if (!params.max) { params.max = 10 }
		if (!params.offset) { params.offset = 0 }
		def sourceInstanceList = new LinkedList<osler.mb.routing.Source>()
		for (def s: rr.sources.source) {
			sourceInstanceList << new osler.mb.routing.Source(name: s.name, accessMethod: s.accessMethod, matchingString: s.matchingString)
		}
		Integer sourceInstanceTotal = sourceInstanceList.size()
		if (params.sort) {
			sourceInstanceList.sort {a,b -> a.compareTo(b) * (params.order?.equals("asc") ? 1 : -1) }
		}
		if ((params.max != -1) && (sourceInstanceList.size() >= new Integer(params.max))) {
			sourceInstanceList = sourceInstanceList[new Integer(params.offset)..(Math.min(new Integer(params.offset)+new Integer(params.max), sourceInstanceTotal)-1)]
		}
		[sourceInstanceList: sourceInstanceList, sourceInstanceTotal: sourceInstanceTotal]
	}
}