Osler Project - Message Broker Console
================

Overview
========
A Grails web application designed to interact with the IBM WebSphere Message Broker server for the University of Ottawa "Osler" project. Message Broker is being used to broker events from various applications in a hub-and-spoke pattern. The console aids in injecting events into the broker and in visualizing what is happening in the broker itself.

Structure
=========
The console is divided into 4 sections: testing, logs, routing rules and receiving messages.

Testing
-------
The testing section is handled by the TesterController class. There are currently 3 functions:

* Inject Test Script: Sending events to the broker using a test script (manual or automatic mode)
* RTLS Simulator: Sending RTLS events to the broker in a user-friendly manner
* Integration Test: Test the functionality of the message broker automatically
	
The TesterController class also contains the code for sending an event via SOAP to the broker. The console can be configured to not send events to the broker, but to simulate sending them instead. This is primarily used in unit-testing.
	
Logging
-------
The log section is handled by the LogController class. There are currently 4 functions:

* Log dashboard: Used to visualize a wide variety of log data at once
* Incoming Log: Tracks the events that entered the message broker
* Response Log: Tracks the responses of the applications that were routed events
* Destination Result Log: Tracks the response of the console to the events it receives through its verifiers (see below)
	
The LogController also exposes 2 REST web services, which collect logging data from the broker. They are:

* /log/logEvent (POST) - Logs an incoming event
* /log/logResponse (POST) - Logs a response to an event from an external application
	
Routing Rules
-------------
The routing rules section is handled by the DestinationController, EventController and SourceController classes. These classes provide the CRUD functionality for the destination, event and source records stored in the routing rules. Additionally, the EventController class handles updates to the routing rules themselves.

The EventController class also exposes one REST web service:

* /event/getDefaultRoutingRules (GET): Used by the broker to fetch an initial copy of the routing rules, should it ever not have it locally.

Receiving Events
----------------
The "receive" section is handled by the ReceiveController. It exposes one REST web service for each type of verification it does. Currently, it exposes:

* /receive/soap (POST): Receives a message via SOAP in the PFM format
* /receive/rest (POST): Receives a message via REST in the PFM format
* /receive/tws (POST): Receives a message via SOAP in the TWS format
* /receive/jms (POST): Receives a message from a JMS queue in the PFM format
	
Handling the Routing Rules
==========================
Special attention must be paid to how the broker maintains its routing rules. Primarily, the rules are kept in the broker's memory. Secondarily, the broker also keeps the latest copy on its local file system. It reads the rules in, if ever they should disappear from memory (re-deployment, power failure, etc...) Lastly, if they are not on the local file system, it will ask for a copy from the console.

The console also maintains a copy on its local file system, called "default-routing-rules.xml" under /WEB-INF/xml. Whenever it updates the routing rules, it also writes the latest version here. It uses this version for caching purposes and also to supply the broker if it should ever need a fresh copy, via the /event/getDefaultRoutingRules web service.

The console interacts with the broker via the classes XmlTransport (abstract superclass) and RestXmlTransport (subclass). RestXmlTransport contains the code to call the read and update web services. Other implementations of XmlTransport are used for testing purposes (i.e. MemXmlTransport for unit-testing and LocalFileXmlTransport for development mode). The transport mechanism used in each deployment environment is configured in Config.groovy and can be seen on the console homepage.

Routing Rules Structure
-----------------------
	<oslerRoutingRules>
		<events></events>
		<destination></destinations>
		<sources></sources>
	</oslerRoutingRules>

The routing rules are composed of three parts: events, destinations and sources. Events and sources are pretty straightforward. Destinations are more complex. Destinations have a method and a format. The method determines what protocol or transport mechanism the events will be delivered by. SOAP is standard, REST works and JMS almost works. The format determines what format the message will be transformed to, before it is delivered to the destination. PFM format is standard and is used internally in the broker, but WBE and TWS are also supported. Adding new methods involves changes to the message flow in the broker. Adding new formats involves creating a new PFM-*.xls file and adding it to the broker. Destinations also contain a list of events, which they receive. Each event should correspondent to an event listed in the root events section.

The formats can be descibed as follows:

* PFM: method-style camelcase. Namespace is used at root but not elsewhere.
		(e.g. `<pat:bedRequest><patientId></patientId</pat: bedRequest>`)	
* TWS: class-style camelcase, but IDs are given as _ID. Namespace is used on every element.
		(e.g. `<tws:BedRequest><tws:Patient_ID></tws:Patient_ID></tws:BedRequest>`)

Tomcat Administration
=====================
* Administrator
* Name: 		oslermbadmin
* Password: 	oslermbadmin

MySQL Administration
====================
* Administrator
	* Name:		root
	* Password:	oslermbadmin
	* From:		localhost
* User
	* Name:		oslermbuser
	* Password:	oslermbuser
	* From:		localhost
	
Source Code
===========
Available on GitHub at:

	git@github.com:cnaphan/osler-mb-console.git

Any development tool would work. I use jEdit. Eclipse-based ones are nice, too.

Gotchas
=======

1.	Unit-testing is not consistently done. Specifically, the routing rules CRUD operations are not well tested. They ought to be, because bugs in those are painful to deal with. They can corrupt the routing rules file.

2.	Namespaces are very rigid. Namespaces are kept in the Config.groovy. They must be exact or else applications will reject the messages.

3.	Date formats are very important. Most applications will fail if they receive a poorly formatted date. The standard format is kept in Config.groovy and is used throughout the console.

4.	Internationalization is not consistently done. I doubt it will be problem, though.

5. A single custom tag is used to output info, warning and error messages. It's pretty trivial, though.

6. The Destination, Source and Event classes may look and act like domain classes but they are not. They implement the Validatable interface, which allows them to be validated, but they are not persisted to a database and do not have methods like list, count, find, etc...

