import java.util.Formatter.DateTime;

import osler.mb.routing.*

class BootStrap {

    def init = { servletContext ->
		/*	
		if (!Source.count()) {
			new Source(name: "BPM", matchingString: "bpm", accessMethod: "SOAP").save(failOnError: true);
			new Source(name: "CEP", matchingString: "pfmon", accessMethod: "SOAP").save(failOnError: true);
		}
		
    	if (!Destination.count() && !Event.count()) {

			Destination pfm = new Destination(name: "PFM", description: "Patient Flow Monitor", url:"http://osler.eecs.uottawa.ca/PFM/services/PFMServer",  accessMethod: "SOAP").save(failOnError: true)
			Destination bpm = new Destination(name: "BPM", description: "IBM WebSphere Lombardi", url:"",  accessMethod: "SOAP").save(failOnError: true)

			def eventNames = ["waitForConsultation1":[pfm,bpm],
							  "consultationCompleted1":[pfm,bpm],
							  "consultationStarted1":[pfm,bpm],
							  "waitForOrderExecution":[pfm,bpm],
							  "orderExecutionCompleted":[pfm,bpm],
							  "waitForBed":[pfm,bpm],
							  "patientArriveInBed":[pfm,bpm],
							  "waitForTransport":[pfm,bpm],
							  "patientTransportStarted":[pfm,bpm],
							  "waitForConsultation2":[pfm,bpm],
							  "consultationStarted2":[pfm,bpm],
							  "consultationCompleted2":[pfm,bpm],
							  "triageScore":[pfm],
							  "patientRegistered":[pfm,bpm],
							  "orderRequest":[pfm,bpm],
							  "orderRequestCompleted":[pfm,bpm],
							  "bedRequest":[pfm,bpm],
							  "patientAdmittedWithNoBed":[pfm,bpm],
							  "patientAdmittedWithBed":[pfm,bpm],
							  "patientTransportRequest":[pfm,bpm]]
			
			eventNames.each { eventName, destinations ->
				def e = new Event(name: eventName)
				destinations.each { d ->
					e.addToDestinations(d)
				}
				if (log.isDebugEnabled()) { log.debug("Registering ${e.toString} with ${e.destinations.size()} destinations") }
				e.save(failOnError: true)
			}		
	
        }	
		*/
		/* Do not need log anymore but maybe later...
		if (!Log.count()) {
			Date now = new Date()
			Date yes = now - 1	
			
			new Log(logTime: new Date(now.year, now.month, now.date, 13, 0, 0), event: "waitForConsultation1", source:"BPM", inputMethod:"HTTP").save(failOnError: true);
			new Log(logTime: new Date(now.year, now.month, now.date, 11, 0, 0), event: "triageScore", source:"BPM", inputMethod:"HTTP").save(failOnError: true);
			new Log(logTime: new Date(now.year, now.month, now.date, 10, 30, 0), event: "patientAdmittedWithNoBed", source:"CEP", inputMethod:"SOAP").save(failOnError: true);
			
			new Log(logTime: new Date(now.year, now.month, now.date, 15, 0, 0), event: "patientAdmittedWithBed", source:"BPM", inputMethod:"HTTP").save(failOnError: true);
			new Log(logTime: new Date(now.year, now.month, now.date, 8, 44, 0), event: "physicianInED", source:"RTLS", inputMethod:"HTTP").save(failOnError: true);
			new Log(logTime: new Date(now.year, now.month, now.date, 8, 44, 0), event: "patientAdmittedWithNoBed", source:"CEP", inputMethod:"SOAP").save(failOnError: true);

			new Log(logTime: new Date(now.year, now.month, now.date, 0, 30, 0), event: "waitForTransport", source:"CEP", inputMethod:"HTTP").save(failOnError: true);
			new Log(logTime: new Date(now.year, now.month, now.date, 18, 44, 0), event: "physicianInED", source:"RTLS", inputMethod:"HTTP").save(failOnError: true);
			new Log(logTime: new Date(now.year, now.month, now.date, 11, 49, 55), event: "patientAdmittedWithNoBed", source:"BPM", inputMethod:"SOAP").save(failOnError: true);

			new Log(logTime: new Date(now.year, now.month, now.date, 10, 00, 30), event: "waitForTransport", source:"CEP", inputMethod:"TEST").save(failOnError: true);
			new Log(logTime: new Date(now.year, now.month, now.date, 10, 15, 0), event: "physicianInED", source:"RTLS", inputMethod:"TEST").save(failOnError: true);
			new Log(logTime: new Date(now.year, now.month, now.date, 10, 05, 45), event: "patientAdmittedWithNoBed", source:"BPM", inputMethod:"TEST").save(failOnError: true);
						
			new Log(logTime: new Date(yes.year, yes.month, yes.date, 13, 0, 0), event: "waitForConsultation1", source:"BPM", inputMethod:"HTTP").save(failOnError: true);
			new Log(logTime: new Date(yes.year, yes.month, yes.date, 20, 22, 0), event: "triageScore", source:"BPM", inputMethod:"HTTP").save(failOnError: true);
			new Log(logTime: new Date(yes.year, yes.month, yes.date, 19, 19, 0), event: "orderRequest", source:"CEP", inputMethod:"SOAP").save(failOnError: true);

		}*/
    }
	
    def destroy = {
    }
	
	private addLogEntry(Integer minutesAgo, String eventName, String source, String inputMethod) {
		GregorianCalendar c = new GregorianCalendar()
		c.add(Calendar.MINUTE, minutesAgo * -1)
		new Log(logTime: c.time, event: eventName, source:source, inputMethod:inputMethod).save(failOnError: true);
	}

	
}
