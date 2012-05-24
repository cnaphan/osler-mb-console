Osler Project - Message Broker Console
================

Overview
========
A Grails web application designed to interact with the IBM WebSphere Message Broker server for the University of Ottawa "Osler" project. Message Broker is being used to broker events from various applications in a hub-and-spoke pattern. The console aids in injecting events into the broker and in visualizing what is happening in the broker itself.

It requires Grails 2.0.3. I use the [rest](http://grails.org/plugin/rest) plugin, which should download automatically when you compile the application. 

The console is probably running at [http://137.122.88.139:8080/osler-mb](http://137.122.88.139:8080/osler-mb).

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

The console also maintains a copy on its local file system, called "local-routing-rules.xml" under /WEB-INF/xml. Whenever it updates the routing rules, it also writes the latest version here. It uses this version for caching purposes.

The console interacts with the broker via the classes XmlTransport (abstract superclass) and RestXmlTransport (subclass). RestXmlTransport contains the code to call the read and update web services. Other implementations of XmlTransport are used for testing purposes (i.e. MemXmlTransport for unit-testing and LocalFileXmlTransport for development mode). The transport mechanism used in each deployment environment is configured in Config.groovy and can be seen on the console homepage.

Routing Rules Structure
-----------------------
	<oslerRoutingRules>
		<events></events>
		<destination></destinations>
		<sources></sources>
	</oslerRoutingRules>

The routing rules are composed of three parts: events, destinations and sources. Events and sources are pretty straightforward. Destinations are more complex. Destinations have a method and a format. The method determines what protocol or transport mechanism the events will be delivered by. SOAP is standard, REST works and JMS almost works. The format determines what format the message will be transformed to, before it is delivered to the destination. TWS (a.k.a Teamworks a.k.a. IBM WebSphere Lombardi) format is standard, but an obsolete PFM format is supported, as well as specialized formats for WBE (WebSphere Business Events) and Active MQ (the JMS queue used by PFM) Destinations also contain a list of events, which they receive. Each event should correspondent to an event listed in the root events section.

The formats can be descibed as follows:

* TWS: class-style camelcase, but IDs are given as _ID. Namespace is used on every element.
		(e.g. `<tws:BedRequest><tws:Patient_ID></tws:Patient_ID></tws:BedRequest>`)
* PFM: method-style camelcase. Namespace is used at root but not elsewhere.
		(e.g. `<pat:bedRequest><patientId></patientId</pat: bedRequest>`)	
* AMQ: JSON-like without braces. Parameters in similar format to TWS. Timestamp in strange colon-less format.
	(e.g. `event:ConsultationCompleted1,Patient_ID:Pa123456,timestamp:2012-05-05/19-15-30`)

Testing Scenarios
=================
There are several ways to test healthcare scenarios in the Olser Project using the console. The overall intent of the console is to streamline, centralize and automate the testing process. It used to require manual file operations and manually tranmission from the Soap UI workbench.

Script-based Testing
--------------------
The first way of generating events is through a test script. A default test script is available within the console web application but specific scenarios ought to be stored in any accessible location (a Desktop or a Dropbox folder). The test script is XML-based and has the following format:

	<oslerTestScript>
		<name>
		<description>
		<event name="AnyEventName" sourceSuffix="">
			...any paramters...
		</event>
		...arbitrary number of <event> elements...
	</oslerTestScript>

The `sourceSuffix` attribute is optional and is used to mark an event as virtually originating from a particular application. Thus, if the true source of the event (i.e. the machine the console is running on) is given by X and the event's sourceSuffix is marked as Y, the source for that particular event will be logged as X-Y.

The test script can contain any events, established ones or not. It can also contain any parameters, which will be copied as is and transmitted to the broker. Note that additional attributes will be lost and if parameters become more complex (nested additional layers), the script may not transmit these accurately.

The test script can be run in manual or automatic mode. Manual is default and allows the user to control the timing of event generation on the page `manual.gsp`. Automatic skips that step and sends all the events immediately.

###Delays in Automated Scripting
One of the downsides of automated event generation is that, if events are generated too quickly, the scenario will not move forward correctly. To counteract this problem, it is a good idea to introduce a small delay between sending events. However, not all events need delay, just the ones that will result in CEP generating an event. These events are always RTLS events, so the automated test script introduces a small 1 second delay after sending an RTLS event (significant by having the word "In" or "Out" in the event name). Changes to this behaviour can be made in the `run` method of the `TesterController` class.

RTLS Simulation
---------------
While the test script is useful for simulating an arbitrary set of events, there appeared to be a need for a simpler way to simulate RTLS events. For demonstrations involving all the applications, the only missing event generator is the RTLS. I went through several revisions of how best to structure such a page and I eventually settled on the People/Location matrix, where there are two buttons per Person and Location, which simulates an In event or an Out event, respectively.

To extend this feature to include different people or locations, you must edit the method `rtlsSimulator` in `TesterController`. The people and locations are defined by two simple maps which are passed to the GSP. If a more dynamic approach is needed (say, to accomodate dozens of patients and locations), either use the test script, which is better suited to that sort of scenario, or make a new page that allows the entry of arbitrary parameter values.

Integration Testing
-------------------
The last type of testing is integration testing, which involves automatically sending out events and waiting for the responses from the broker, and then checking to ensure they are proper. The test uses the same test scripts as the first testing method, but it is advisable to use fewer tests, so the results are a bit more legible. Of course, the broker could also be load-tested by using a massive integration test script with hundreds of events. The current integration test sends out "test" events, which are only delivered to the test destinations. Thus, it only tests the functioning of the console and broker - not the destination applications.

A different type of integration test might be desirable in the future, where the destination applications are automatically invoked and tested. In other words, a full demonstration is run and the states of the various applications are checked and validated at each step. A few issues arise. First, it is difficult to automatically control the timing of sending events currently. The time needed to run such a test would exceed what is possible for a single page load, and thus, the test would have to be initiated as a background process, so the programming would necessarily be rather complex. Ideally, you could send events as quickly as possible, but that isn't possible right now. Another issue is the difficulty in checking the state of the various applications, beyond the events they generate (or not). PFM could be extended with a bunch of GET REST services, used for other applications to check internal patient states, and these could be used for automated testing. Another issue is the difficulty in "reseting" the internal state of the applications (WBE and BPM particularly) to ensure a clean test.

Source Code
===========
Available on GitHub at:

	git@github.com:cnaphan/osler-mb-console.git
	
To get a fresh copy of the source code, navigate to your workspace directory and then:

* `mkdir osler-mb-console`
* `cd osler-mb-console`
* `git init`
* `git remote add origin git@github.com:cnaphan/osler-mb-console.git`
* `git pull origin master`

Any development tool would work. I use jEdit. Eclipse-based ones are nice, too.

Running the Console
-------------------
To run the console, you need to set up the database schema. To do this, I export a schema from Grails and manully run it against a fresh database, usually called `osler_mb_prod`. To export the schema, I use the Grails command `grails prod schema-export`, which should generate a proper MySQL schema in the `target` folder.

Secondly, you need to install Tomcat. I use Version 7 on Windows. I have found there to be some memory issues so I increase the memory available to the Tomcat JVM to 1500 MB.

Thirdly, generate the console WAR using the Grails command `grails prod war osler-mb.war` and copy the WAR file into the `%TOMCAT_HOME%/webapps` directory. Start up Tomcat and the WAR should auto-deploy. If it deploys but the Tomcat Application Manager says that it is "stopped", then there was an internal problem in the bootstrap sequence. Check the Tomcat logs. I have found problems related to the GORM initialization, as well as memory issues. 

Checking the Console's Logs
---------------------------
The console's log is output to `%TOMCAT_HOME%/logs/osler-mb.txt`. At the `info` level, it will list basic operations undertaken, events sent and received, as well as warnings and errors. At the `debug` level, it will provide messages at the start and end of most controllers, as well inside loops and such. I suggest keeping it at `info` or `warn` while in production.
	
Accounts & Passwords
--------------------
###Tomcat Administration

* Administrator
	* Name: 		oslermbadmin
	* Password: 	oslermbadmin

###MySQL Administration

* Administrator
	* Name:			root
	* Password:		oslermbadmin
	* From:			localhost
* User
	* Name:			oslermbuser
	* Password:		oslermbuser
	* From:			localhost

Gotchas
=======

1.	Unit-testing is not consistently done. Specifically, the routing rules CRUD operations are not well tested. They ought to be, because bugs in those are painful to deal with. They can corrupt the routing rules file.

2.	Namespaces are very rigid. Namespaces are kept in the Config.groovy. They must be exact or else applications will reject the messages.

3.	Date formats are very important. Most applications will fail if they receive a poorly formatted date. The standard format is kept in Config.groovy and is used throughout the console.

4.	Internationalization is not consistently done. I doubt it will be problem, though.

5. A single custom tag is used to output info, warning and error messages. It's pretty trivial, though.

6. The `Destination`, `Source` and `Event` classes may look and act like domain classes but they are not. They implement the `Validatable` interface, which allows them to be validated, but they are not persisted to a database and do not have methods like list, count, find, etc...

7. The test script expects no attributes and a simple set of name-value sub-elements. If you need events with attributes or with nested sub-elements, changes to the console code need to be made to the private function `parseEvents` in `TesterController`.
