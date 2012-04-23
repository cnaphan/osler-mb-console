package osler.mb.routing

class Log {	
	
	Date logTime;
	String event;
	String source;
	String inputMethod;

	static mapping = {
		table "log_entry"
		logTime index:"IX_log_logTime"
		
		// If Message Broker inserts into this table, uncomment the following line to disable Grails caching
		//cache false // Do not cache this table because it is mainly updated by Message Broker, not through GORM
	}
	
    static constraints = {
		event(blank: false, maxSize: 50)
		source(blank: false, maxSize: 20)		
		inputMethod(blank: false, maxSize: 10)
    }
	

}
