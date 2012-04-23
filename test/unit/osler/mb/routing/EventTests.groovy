package osler.mb.routing



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
class EventTests {

    void testProperties() {
	   def newEvent = new Event()
	   newEvent.name = null
	   assert !newEvent.validate()
	   newEvent.name = ""
	   assert !newEvent.validate()
	   newEvent.name = "12345678901234567890123456789012345678901234567890X"
	   assert !newEvent.validate()
	   newEvent.name = "EventName"
	   assert newEvent.validate()
    }
	
}
