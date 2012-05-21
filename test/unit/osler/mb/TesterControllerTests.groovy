package osler.mb

import grails.test.mixin.*
import org.junit.*
import osler.mb.routing.*

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(TesterController)
@Mock(Log)
class TesterControllerTests {

	void testIndex() {
		def model = controller.index()
		assert model.xmlResources.size() > 0
		assert model.modeList.size() > 0
	}
	
    void testRun() {
		params.mode = 2
		request.fileMap["testScript"] = new org.springframework.mock.web.MockMultipartFile("osler-test-script.xml",
			"""
<oslerTestScript>
	<testName>Default Osler Test Script</testName>
	<event name="patientAdmittedWithBed">
		<patientId>Pa123456</patientId>
		<unitId>CCU</unitId>
		<bedId>Bed207</bedId>
		<timestamp>2012-03-12T02:08:16</timestamp>
	</event>
	<event name="consultationCompleted2">
		<patientId>Pa123456</patientId>
		<providerId>Phy777777</providerId>
		<timestamp>2012-03-12T02:08:16</timestamp>
	</event>
</oslerTestScript>
			""" as byte[])
		controller.run()	
		assert flash.message?.contains("success.message")	
		assert flash.errors.size() == 0
		assert Log.count() == 2
		
	}
	
	void testSend() {
		params.method = "MethodName"
		params.body = ""
		controller.send()
		assert controller.response.status == 501
		assert Log.count() == 0		
		params.body = "<eventName sourceSuffix='RTLS'><patientId>pa1245</patientId><timestamp>2012-04-15 12:30:15</timestamp></eventName>"
		controller.send()
		assert controller.response.status == 200
		assert controller.response.contentAsString.contains("Sent")		
		assert Log.count() == 1	
		assert Log.get(1).source == "TEST-RTLS"
	}	
		
	void testExtractSuffix() {
		assert controller.extractSuffix("<event sourceSuffix='RTLS'>") == "-RTLS"
		assert controller.extractSuffix("<event suffix='RTLS'>") == ""
		assert controller.extractSuffix('<event sourceSuffix="George" job="Politician">') == '-George'
		assert controller.extractSuffix("""<patientInCCU sourceSuffix='RTLS'>
  <locationId>CCU12</locationId>
  <timestamp>2012-04-20T16:31:34</timestamp>
</patientInCCU>""") == "-RTLS"
	}
	
	void testCleanTime() {
		assert controller.cleanTime("2012-03-12 01:37:40Z") == "2012-03-12T01:37:40"
		assert controller.cleanTime(" 2012-03-12 01:37:40Z ") == "2012-03-12T01:37:40"
		assert controller.cleanTime("2012-03-12 01:37:40") == "2012-03-12T01:37:40"
		assert controller.cleanTime("2012-03-12T01:37:40Z") == "2012-03-12T01:37:40"
		assert controller.cleanTime("2012-03-12 01:37Z") == "2012-03-12T01:37"
	}
}
