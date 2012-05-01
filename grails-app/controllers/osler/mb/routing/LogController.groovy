package osler.mb.routing

import java.util.List;

class LogController {

	private static final Integer ITEMS_PER_PAGE = 10
	
	private static final Integer VIEW_BY_HOUR = 1
	private static final Integer VIEW_BY_DAY = 2
	private static final Integer VIEW_BY_WEEK = 3
	private static final Integer VIEW_BY_ALL= 4
	private static final Map TIME_HOUR_MAP = [(VIEW_BY_HOUR): 1, (VIEW_BY_DAY): 24, (VIEW_BY_WEEK): 24*7, (VIEW_BY_ALL): 0]	
	
	static allowedMethods = [logEvent: "POST", logResponse: "POST" ]
	
	/**
	 * Builds up some maps to help in generating the dashboard widgets. 
	 * Note: Since it iterates across database results, it would be much more efficient to use various COUNT and GROUP BY's
	 * to build these statistics.
	 */
	def index = {
		def viewByMap = [(VIEW_BY_HOUR): "the last hour", (VIEW_BY_DAY): "the last day", (VIEW_BY_WEEK): "the last week", (VIEW_BY_ALL): "forever"]
		Integer by = params.viewfor ? Integer.parseInt(params.viewfor) : LogController.VIEW_BY_HOUR	

		def byInput = [:]
		def bySource = [:]
		def byType = [:]
		def byTime = [:]
		def byDest = [:]
		
		// Use the incoming log entries to build some statistics
		def logItems = this.listByTime(by)
		if (log.isDebugEnabled()) { log.debug("index: Found ${logItems.size()} log entries for '${by}'") }
		logItems.each{ Log l ->
			if (byInput.get(l.inputMethod) > 0) {
				byInput.put(l.inputMethod, byInput.get(l.inputMethod)+1)
			} else {
				byInput.put(l.inputMethod, 1)
			}			
			if (bySource.get(l.source) > 0) {
				bySource.put(l.source, bySource.get(l.source)+1)
			} else {
				bySource.put(l.source, 1)
			}
			
			// If the first character is in the upper case, translate it to lower case
			String eventKey = l.event[0].tr('A-Z','a-z') + l.event[1..l.event.length()-1]			
			if (byType.get(eventKey) > 0) {
				byType.put(eventKey, byType.get(eventKey)+1)
			} else {
				byType.put(eventKey, 1)
			}
			
			// For time, create a new date to use
			Date d = new Date(l.logTime.time)
			// For categories greater than "by week", nullify the hour
			if (by > VIEW_BY_WEEK) { d.hours = 0 }
			// For categories other than "by hour", nullify the minute
			if (by != VIEW_BY_HOUR) { d.minutes = 0 }
			Date key = new Date(d.year, d.month, d.date, d.hours, d.minutes, 0)			
			if (byTime[key] != null) {
				byTime[key][6]++
			} else {
				byTime[key] = [d.year + 1900, d.month, d.date, d.hours, d.minutes, 0, 1]	
			}
		}		

		// Use the response log entries to build some statistics
		def responseLogItems = this.listResponsesByTime(by)
		responseLogItems.each{ ResponseLog l ->
			String destinationAndCodeKey = "${l.destinationName}-${l.responseStatusCode}"
			if (byDest.get(destinationAndCodeKey) > 0) {
				byDest.put(destinationAndCodeKey, byDest.get(destinationAndCodeKey)+1)
			} else {
				byDest.put(destinationAndCodeKey, 1)
			}
		}
		
		
		[viewfor:by,
		eventsByHour:byTime,
		eventsBySource:bySource,
		eventsByInput:byInput,
		eventsByType:byType.sort {a,b -> a.key.compareTo(b.key) },
		responsesByDestinationAndStatusCode: byDest.sort {a,b -> a.key.compareTo(b.key) },
		viewByMap:viewByMap	
		]
				
	}
	
	def list = {		
		// Set some default parameters, if they are not set
		if (!params.sort) { params.sort = "logTime" }
		if (!params.order) { params.order = "DESC" }
		if (!params.max) { params.max = LogController.ITEMS_PER_PAGE }		
		def logList
		def logCount
		
		if (params.fromdate || params.todate) {
			def dates = this.parseDates(params)			

			if (dates.from != null) {
				// If we have a from date, use both to get the logs between them				
				logList = Log.findAllByLogTimeBetween(dates.from,dates.to,params)				
				logCount = Log.countByLogTimeBetween(dates.from,dates.to,params)
				if (log.isDebugEnabled()) { log.debug("list: Retrieved ${logCount} log entries between ${dates.from.format(grailsApplication.config.osler.mb.dateFormat)} and ${dates.to.format(grailsApplication.config.osler.mb.dateFormat)}") }
			} else {
				// If we only have one date, get everything before it
				logList = Log.findAllByLogTimeLessThanEquals(dates.to, params)
				logCount = Log.countByLogTimeLessThanEquals(dates.to, params)
				if (log.isDebugEnabled()) { log.debug("list: Retrieved ${logCount} log entries before ${dates.to.format(grailsApplication.config.osler.mb.dateFormat)}") }			
			}
		} else {
			// If we have neither dates, just get everything
			logList = Log.list(params)
			logCount = Log.count()
		    if (log.isDebugEnabled()) { log.debug("list: Retrieved ${logCount} log entries") }		
		}
		[logInstanceList: logList, logInstanceTotal: logCount, logDateFormat: grailsApplication.config.osler.mb.dateFormat]
	}
	
	def responseLogList = {
		// Set some default parameters, if they are not set
		if (!params.sort) { params.sort = "logTime" }
		if (!params.order) { params.order = "DESC" }
		if (!params.max) { params.max = LogController.ITEMS_PER_PAGE }		
		def logList
		def logCount
		
		if (params.fromdate || params.todate) {
			def dates = this.parseDates(params)			

			if (dates.from != null) {
				// If we have a from date, use both to get the logs between them				
				logList = ResponseLog.findAllByLogTimeBetween(dates.from,dates.to,params)				
				logCount = ResponseLog.countByLogTimeBetween(dates.from,dates.to,params)
				if (log.isDebugEnabled()) { log.debug("list: Retrieved ${logCount} response log entries between ${dates.from.format(grailsApplication.config.osler.mb.dateFormat)} and ${dates.to.format(grailsApplication.config.osler.mb.dateFormat)}") }
			} else {
				// If we only have one date, get everything before it
				logList = ResponseLog.findAllByLogTimeLessThanEquals(dates.to, params)
				logCount = ResponseLog.countByLogTimeLessThanEquals(dates.to, params)
				if (log.isDebugEnabled()) { log.debug("list: Retrieved ${logCount} response log entries before ${dates.to.format(grailsApplication.config.osler.mb.dateFormat)}") }			
			}
		} else {
			// If we have neither dates, just get everything
			logList = ResponseLog.list(params)
			logCount = ResponseLog.count()
		    if (log.isDebugEnabled()) { log.debug("list: Retrieved ${logCount} response log entries") }		
		}
		[logInstanceList: logList, logInstanceTotal: logCount, logDateFormat: grailsApplication.config.osler.mb.dateFormat]
	}
	
	/**
	 * A REST web service invoked by Message Broker using POST. Contains a small XML message with the format
	 * <logEvent><event>someEvent</event><source>someSource</source><inputMethod>someInputMethod</inputMethod><sentToJms></sentToJms></logEvent>
	 * Parses the message and saves the log. If there's an error (malformed XML or invalid parameters),
	 * responds with status 500. If the save is successful, responds with status 200.
	 */
	def logEvent = {
		try {		
			def logEvent = request.XML			
			new Log(logTime: new Date(), 
					event: logEvent.event.text(), 
					source: logEvent.source.text(), 
					inputMethod: logEvent.inputMethod.text(),
					numSentPubSub: logEvent.numSentPubSub.text(),
					numSentP2P: logEvent.numSentP2P.text()
				).save(failOnError: true) 
			log.info("Event ${logEvent.event.text()} received for logging from ${request.getRemoteHost()}")
			render(text: "Log successful", status: 200) // Respond with 200 Ack
		} catch (grails.validation.ValidationException e) {
			log.error("Error saving log: ${e.getMessage()}")
			render (text: "There was an error saving the log message due to missing or malformed parameters.", status: 500)
		} catch (Exception e) {
			log.error("Failed while processing event for log: ${e.getMessage()}")
			render (text:  e.getMessage(), status: 500) // Respond with 500 server error
		}
	}
	
	def logResponse = {
		try {		
			def logResponse = request.XML			
			new ResponseLog(logTime: new Date(), 
					event: logResponse.event.text(), 
					destinationName: logResponse.destinationName.text(),
					accessMethod: logResponse.accessMethod.text(),
					responseStatusCode: Integer.parseInt(logResponse.responseStatusCode.text())
				).save(failOnError: true) 
			log.info("Response event ${logResponse.event.text()} received for logging from ${request.getRemoteHost()}")
			render(text: "Log successful", status: 200) // Respond with 200 Ack
		} catch (grails.validation.ValidationException e) {
			log.error("Error saving log: ${e.getMessage()}")
			render (text: "There was an error saving the response log message due to missing or malformed parameters.", status: 500)
		} catch (Exception e) {
			log.error("Failed while processing event for response log: ${e.getMessage()}")
			render (text:  e.getMessage(), status: 500) // Respond with 500 server error
		}
	}
	
	/**
	 * Parses the form and to dates in the parameters into a nice map
	 */
	private Map parseDates (def params) {
		Date f // from date
		Date t // to date
		// Try to parse the given from date
		if (params.fromdate) {
			try {
				f = Date.parse(grailsApplication.config.osler.mb.dateFormat, params.fromdate)
			} catch (java.text.ParseException e) {
				flash.error = "Invalid 'from' date. Must be in the format ${grailsApplication.config.osler.mb.dateFormat}"
				log.debug("Couldn't parse from date: ${params.fromdate}")
				f = null
			}
		} else {
			f = null
		}
		
		// Try to parse the given todate
		if (params.todate) {
			try {
				t = Date.parse(grailsApplication.config.osler.mb.dateFormat, params.todate)
			} catch (java.text.ParseException e) {
				flash.error = "Invalid 'to' date. Must be in the format ${grailsApplication.config.osler.mb.dateFormat}"
				log.debug("Couldn't parse to date: ${params.todate}")
				t = new Date()
			}
		} else {
			t = new Date()
		}
		return [from: f, to: t]		
	}
	
	private List listByTime(Integer by) {		
		Integer numHours = TIME_HOUR_MAP.get(by)		
		if (numHours > 0) {
			GregorianCalendar now = new GregorianCalendar()
			GregorianCalendar yesterday = new GregorianCalendar()
			yesterday.add(Calendar.HOUR, -1 * numHours)
			return Log.findAllByLogTimeBetween(yesterday.getTime(),new Date(),[sort:"logTime", order:"ASC"])
		} else {
			return Log.list([sort:"logTime", order:"ASC"])
		}
	}	
	
	private List listResponsesByTime(Integer by) {		
		Integer numHours = TIME_HOUR_MAP.get(by)		
		if (numHours > 0) {
			GregorianCalendar now = new GregorianCalendar()
			GregorianCalendar yesterday = new GregorianCalendar()
			yesterday.add(Calendar.HOUR, -1 * numHours)
			return ResponseLog.findAllByLogTimeBetween(yesterday.getTime(),new Date(),[sort:"logTime", order:"ASC"])
		} else {
			return ResponseLog.list([sort:"logTime", order:"ASC"])
		}
	}	
}
