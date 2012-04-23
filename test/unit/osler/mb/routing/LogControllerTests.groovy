package osler.mb.routing

import org.junit.*
import grails.test.mixin.*


@TestFor(LogController)
@Mock(Log)
class LogControllerTests {

	void testIndex() {
		def result = controller.index()
		assert result.viewfor == 2
		assert result.viewByMap.size() == 4
		assert result.eventsByHour.size() == 0
		assert result.eventsBySource.size() == 0
		assert result.eventsByInput.size() == 0
		assert result.eventsByType.size() == 0
		
		addLogEntry(30, "consultationCompleted1")
		addLogEntry(45, "waitForBed")
		addLogEntry(55, "waitForConsultation1")
		addLogEntry(75, "orderExecutionCompleted")
		addLogEntry(75, "waitForBed")
		addLogEntry(75, "bedRequest")
		addLogEntry(120, "patientAdmittedWithNoBed")
		addLogEntry(140, "patientAdmittedWithNoBed")
		
		result = controller.index()		
		assert result.eventsByHour.size() == 3
		assert result.eventsBySource.size() == 1
		assert result.eventsBySource["TEST"] == 8

		assert result.eventsByInput.size() == 1
		assert result.eventsByType.size() == 6
		Integer sum = 0
		result.eventsByHour.each { key, value ->
			assert key instanceof Date 
			assert value[5] == 0
			sum = sum + value[6]
		}	
		assert sum == 8
	}
	
	void testList() {
		def result = controller.list()		
		assert result.logInstanceList.size() == 0
		assert result.logInstanceTotal == 0

		this.addLogEntry(60, "waitForConsultation1")
		this.addLogEntry(75, "triageScore")
		
		result = controller.list()
		assert result.logInstanceList.size() == 2
		assert result.logInstanceTotal == 2	
		
		this.addLogEntry(85, "waitForConsultation2")
		this.addLogEntry(160, "discharge")
		
		result = controller.list()
		assert result.logInstanceList.size() == 4
		assert result.logInstanceTotal == 4
		
		params.max = 2
		result = controller.list()
		assert result.logInstanceList.size() == 2
		assert result.logInstanceTotal == 4
		
		// Check if the todate works (80 minutes ago, 2 events)
		params.max = null
		GregorianCalendar c1 = new GregorianCalendar(2012, 4, 1, 12, 0, 0, 0)
		c1.add(Calendar.MINUTE, -80)
		params.todate = c1.time.format(grailsApplication.config.osler.mb.dateFormat)
		result = controller.list()
		assert result.logInstanceList.size() == 2
		assert result.logInstanceTotal == 2
		
		// Check if the fromdate works (70 minutes ago, 1 event)
		params.todate = null
		GregorianCalendar c2 = new GregorianCalendar(2012, 4, 1, 12, 0, 0, 0)
		c2.add(Calendar.MINUTE, -70)
		params.fromdate = c2.time.format(grailsApplication.config.osler.mb.dateFormat)
		result = controller.list()
		assert result.logInstanceList.size() == 1
		assert result.logInstanceTotal == 1

	}
	
	
	void testLogEvent() {
		assert Log.count() == 0		
		request.XML = """<logEvent><event>waitForConsultation1</event><source>TEST</source><inputMethod>SOAP</inputMethod></logEvent>"""
		controller.logEvent()		
		assert response.status == 200		
		assert Log.count() == 1
		
		// Test bad input
		request.XML = """<logEvent><event>waitForConsultation1</event><source>TEST</source><inputMethod>SOAP</inputMethod></lognt>"""
		controller.logEvent()
		assert response.status == 500
		assert Log.count() == 1

	}
	
	private addLogEntry(Integer minutesAgo, String eventName) {
		GregorianCalendar c = new GregorianCalendar(2012, 4, 1, 12, 0, 0, 0)
		c.add(Calendar.MINUTE, minutesAgo * -1)
		new Log(logTime: c.time, event: eventName, source:"TEST", inputMethod:"TEST").save(failOnError: true);
	}

}
