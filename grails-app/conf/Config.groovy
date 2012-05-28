// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']


// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true


// Configuration some elements that pertain to Message broker
osler.mb.dateFormat="yyyy-MM-dd'T'HH:mm:ss"
osler.mb.brokerUrl="137.122.88.139"

// Register Event configuration
osler.mb.registerEventUrls=[SOAP:"http://${osler.mb.brokerUrl}:7080/soap/registerEvent-tws",
							REST:"http://${osler.mb.brokerUrl}:7080/rest/registerEvent-tws"]
osler.mb.registerEventMethod="DIRECT" // DIRECT or SOAP
							
// Routing Rules configuration			
osler.mb.getRoutingRulesUrl="http://${osler.mb.brokerUrl}:7080/rest/getRoutingRules"
osler.mb.updateRoutingRulesUrl="http://${osler.mb.brokerUrl}:7080/rest/updateRoutingRules"
osler.mb.routingRulesTransportMode = "MEM" // MEM, LOCAL or REST

// Namespaces used by the console
osler.mb.soapNamespace = "http://schemas.xmlsoap.org/soap/envelope/"
osler.mb.pfmNamespace = "http://patientflowmonitoring/"
osler.mb.twsNamespace="http://WIN-687RHJV6VUL:19086/teamworks/webservices/OPPOD/WFMCoordinationEventService.tws"
osler.mb.eventNamespace = osler.mb.twsNamespace // The namespace used by events sent out by the console

environments {
    development {
        grails.serverURL = "http://localhost:8080/osler-mb"
        grails.logging.jul.usebridge = true
		osler.mb.registerEventMethod="SOAP"
		osler.mb.routingRulesTransportMode = "LOCAL" // Work with the default routing rules file in local mode
    }
    production {
        grails.serverURL = "http://137.122.88.139:8080/osler-mb"
        grails.logging.jul.usebridge = false
		osler.mb.registerEventMethod="SOAP"
		osler.mb.routingRulesTransportMode = "REST"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')			
    }
	   
	environments {		
		production {
			appenders {			
				rollingFile name: "rollingFile",
                    maxFileSize: 1024,
                    file: "C:\\tomcat7\\logs\\osler-mb.txt"

			jdbc name:"jdbc",
				URL:"jdbc:mysql://137.122.88.139/osler_mb_prod?useUnicode=yes&characterEncoding=UTF-8",
				driver: "com.mysql.jdbc.Driver",
				user: "oslermbuser",
				password: "oslermbuser", 
				sql: "INSERT INTO system_log VALUES('%x','%d','%C','%p','%m')"			
                    
    		}
    		
		}
	}
	

    error ['org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate',
		   'org.springframework.core.env.StandardEnvironment']
		   
    info  ['grails.app.controllers',
    	   'osler.mb.routing']
				
}
