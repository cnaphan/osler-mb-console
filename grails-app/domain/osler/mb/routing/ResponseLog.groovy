package osler.mb.routing

class ResponseLog {	
	
	Date logTime
	String event
	String destinationName
	String accessMethod
	Integer responseStatusCode

	static mapping = {
		table "response_log_entry"
		logTime index:"IX_response_log_logTime"
		
		// If Message Broker inserts into this table, uncomment the following line to disable Grails caching
		//cache false // Do not cache this table because it is mainly updated by Message Broker, not through GORM
	}
	
    static constraints = {
		event(blank: false, maxSize: 50)
		destinationName(blank: false, maxSize: 50)
		accessMethod(blank: false, maxSize: 50)		
    }
	

}
