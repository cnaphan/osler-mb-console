package osler.mb.routing

import org.junit.*
import grails.test.mixin.*

@TestFor(EventController)
class EventControllerTests {
/*
	@Ignore("File IO doesn't work in test")
	void testGetDefaultRoutingRules() {
		controller.getDefaultRoutingRules()
		assert response.status == 200
		assert response.contentAsString?.size() > 0
		def rr = new XmlSlurper(true, false).parseText(response.contentAsString)
		assert rr
		assert rr.events.event.size() > 0
		
	}
	*/
	
	void testList () {
		assert XmlTransport.getInstance() instanceof MemXmlTransport // Assure that we are usign the mem xml transport
		params.offset = 0
		params.max = 3
		params.sort = "name"
		params.order = "desc"
		def results = controller.list()
		assert results.eventInstanceList.size() == 3
		assert results.eventInstanceTotal == 5
		assert results.eventInstanceList[0].name == "patientRegistered"			// name DESC 1
		assert results.eventInstanceList[1].name == "patientAdmittedWithBed" 	// name DESC 2
		assert results.eventInstanceList[2].name == "consultationStarted1" 		// name DESC 3
	}
	
	void testUpdateRouting() {
		def trans = XmlTransport.getInstance()
		assert trans instanceof MemXmlTransport // Assure that we are usign the mem xml transport
		def rr = trans.getRoutingRules()
		def destinations = trans.listDestinations(rr, [sort: "name", order: "asc", max: -1]).destinationInstanceList
		assert destinations.size() == 3
		def testRest = trans.getDestinationByName(rr, [id: "TestRest"])
		assert testRest.events.size() == 3
		
		params.paths = [:]
		
		for (def d : destinations) {
			assert d.events.size() > 2
			for (def e : d.events) {				
				def key = "${d.name}.${e}".toString()				
				params.paths[key] = "on"				
			}
		}
		
		assert params.paths.size() == (5 + 3 + 3)
		
		// Remove one path and add one
		assert params.paths["TestTws.patientAdmittedWithBed"] == "on"
		assert !params.paths["TestRest.patientAdmittedWithBed"]
		params.paths.remove("TestTws.patientAdmittedWithBed")
		params.paths["TestRest.patientAdmittedWithBed"] = "on"
		
		controller.updaterouting()
		assert flash.message == "osler.mb.routing.EventRouting.updated.message"
		
		rr = trans.getRoutingRules() 
		testRest = trans.getDestinationByName(rr, [id: "TestRest"])
		assert testRest.events.size() == 4
		
	}
}
