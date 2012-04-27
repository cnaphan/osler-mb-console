package osler.mb.routing

import org.junit.*
import grails.test.mixin.*

@TestFor(EventController)
class EventControllerTests {

	void testGetDefaultRoutingRules() {
		controller.getDefaultRoutingRules()
		assert response.contentAsString?.size() > 0
		def rr = new XmlSlurper(true, false).parseText(response.contentAsString)
		assert rr != null
		assert rr.events.event.size() > 0
		
	}
	
	void testUpdateRouting() {
		//TODO Write tests for updating the xml - need to mock the xml transporter
	}
}
