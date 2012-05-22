package osler.mb.routing

import org.codehaus.groovy.grails.validation.Validateable

@Validateable
class Source {
	
	String name	
	String accessMethod
	String matchingString	

    static constraints = {
		name blank: false, maxSize: 6,
			 validator: { if (it?.contains(" ")) return ["has.whitespace.message"] }
	    accessMethod maxSize: 10
		matchingString blank: false
    }
	
	public Source(def params) {
		if (params) { this.applyProperties(params) }
	}
	
	public void applyProperties(def params) {
		this.name = params.name
		this.accessMethod = params.accessMethod
		this.matchingString = params.matchingString
	}
	
}
