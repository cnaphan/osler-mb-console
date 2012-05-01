package osler.mb.routing
import org.codehaus.groovy.grails.validation.Validateable

@Validateable
class Destination implements Comparable  {
	
	String name
	String description
	String url
	String accessMethod
	Boolean disabled = false
	def events = []
	
    static constraints = {
		name maxSize: 50, blank: false, validator: { if (it?.contains(" ")) return ["has.whitespace"] }
		description maxSize: 500, nullable: true, blank: true
		accessMethod inList: ["SOAP", "HTTP", "MQ", "JMS"], validator: checkMethod	
		url nullable: true, url:true	
    }
	
	public Destination(def params) {
		if (params) { this.applyProperties(params) }
	}
	
	public void applyProperties(def params) {		
		this.name = params.name
		this.description = params.description
		this.url = params.url
		this.accessMethod = params.accessMethod
		this.disabled = params.disabled ? true : false;
		if (params.events) { this.events = params.events }
	}
	

	
	String toString() { return "Destination[name:${name}]" }
	
	int compareTo(Object b) {
		return this.name.compareToIgnoreCase(((Destination) b).name)
	}
	
	/**
	 * A validator used to catch inconsistencies in the access method versus other parameters
	 */
	static checkMethod = { val, obj ->
		if (val in ["MQ", "JMS"] && obj.url?.size() > 0) {
			return ['osler.mb.routing.Destination.urlWithQueue.message'] 
		}
	}
	
}