package osler.mb.routing



/*import grails.test.mixin.*
import org.junit.**/

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Log)
class LogTests {
	
	void testProperties() {
		def l1 = new Log(logTime: new Date(), event: "testName", source: "UTEST", inputMethod: "UTEST")
		assert l1.validate()		

		def l2 = new Log(logTime: new Date(), source: "UTEST", inputMethod: "UTEST")
		assert !l2.validate()

	}

}