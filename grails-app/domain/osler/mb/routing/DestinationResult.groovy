package osler.mb.routing

class DestinationResult {

	Date logTime
	String event
	String method
	String remoteHost
	String errorXml
	
	static constraints = {
		event(blank: false, maxSize: 50)
		method(blank: false, maxSize: 15)
		remoteHost(blank: false, maxSize: 150)
		errorXml(nullable: true, blank: true, maxSize: 2000)
    }

}
