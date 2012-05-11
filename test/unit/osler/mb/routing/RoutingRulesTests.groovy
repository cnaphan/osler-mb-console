package osler.mb.routing

import grails.test.mixin.*
import org.junit.*
import grails.test.*

class RoutingRulesTests extends GrailsUnitTestCase {
	
	void testRoutingRules() {
		def trans = XmlTransport.getInstance()
		assert trans instanceof MemXmlTransport
		def rr = trans.getRoutingRules()
		assert rr.events.event.size() == 5 
		assert rr.sources.source.size() == 2
		
		rr.events.appendNode { event("newEvent") }
		
		//assert rr.events.event.size() == 6		
		assert trans.updateRoutingRules(rr)
		
		def rr2 = trans.getRoutingRules()
		assert rr2.events.event.size() == 6
		
		def e = trans.getEventByName(rr, [id:"patientAdmittedWithBed"])
		assert e instanceof Event
		assert e.name == "patientAdmittedWithBed"
		assert e.destinations?.size() == 2
		
		def d = trans.getDestinationByName(rr, [id: "TestSoap"])
		assert d instanceof Destination
		assert d.name == "TestSoap"
		assert d.accessMethod == "SOAP"
		assert d.format == "PFM"
		assert d.events.size() == 5		
		
		
	}
}