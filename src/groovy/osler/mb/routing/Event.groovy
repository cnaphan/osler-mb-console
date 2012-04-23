package osler.mb.routing

import org.codehaus.groovy.grails.validation.Validateable

@Validateable
class Event implements Comparable {
	

	String name
	def destinations = []
	
	static constraints = {
		name(blank: false, maxSize: 50,
			 validator: { if (it?.contains(" ")) return ["has.whitespace.message"]} )
	}

	public Event(def params) {
		if (params) { this.applyProperties(params) }
	}
	public void applyProperties(def params) {
		this.name = params.name
		if (params.destinations) { this.destinations = params.destinations }
	}
	
	String toString() { return "XMLEvent[name:${name}]" }
	
	int compareTo(Object b) {
		return this.name.compareToIgnoreCase(((Event) b).name)
	}
	
}