package osler.mb.routing

import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
class SourceTests {

    void testValidate() {		
		Source s = new Source(matchingString: "XXX", accessMethod: "SOAP")
		mockForConstraintsTests(Source, [ s ])
		s.name = "BPM"
		assert s.validate()
		s.name = null
		assert !s.validate()
		assertEquals "nullable", s.errors["name"]
		s.name = ""
		assert !s.validate()
		s.name = "BPMBPMX"
		assert !s.validate()
		s.name = "B M"
		assert !s.validate()
		assertEquals "has.whitespace.message", s.errors["name"]
		s.name = " "
		assert !s.validate()
		s.name = "BPM "
		assert !s.validate()
		s.name = "SIXLET"
		assert s.validate()
		log.info("Finished")
    }

}
