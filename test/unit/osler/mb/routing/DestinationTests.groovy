package osler.mb.routing



import grails.test.mixin.*
import org.junit.*

import osler.mb.routing.Destination;

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */

class DestinationTests {

    void testURL() {
		Destination d = new Destination(name:"test")
		// Test some standard URLs	  
		d.url = "http://fsa4.site.uottawa.ca/"
		assert d.validate()
		d.url = "http://fsa4.site.uottawa.ca:7500/"
		assert d.validate()
		d.url = "https://fsa4.site.uottawa.ca:7500/"
		assert d.validate()
		// No protocol
		d.url = "www.google.com"
		assert !d.validate()
		// With sub-folders
		d.url = "http://fsa4.site.uottawa.ca/sub-folder/"
		assert d.validate()
		// With sub-folders and file
		d.url = "http://fsa4.site.uottawa.ca/sub-folder/file.htm"
		assert d.validate()
		// With sub-folders, file and parameters
		d.url = "http://fsa4.site.uottawa.ca/sub-folder/file.htm?q=1"
		assert d.validate()
		// Weird URL but otherwise valid
		d.url = "protocol://domain.name:9/"
		assert d.validate()
		// Try an IP address with no protocol (bad)
		d.url="137.122.93.183"		
		assert !d.validate()
		// Try an IP address with port and protocol
		d.url="http://137.122.93.183:7050"
		assert d.validate()
		// Bad form
		d.url="www"
		assert !d.validate()
		d.url = "/www.google.com"
		assert !d.validate()
		d.url = "fsa:www.google.com"
		assert !d.validate()
		d.url = "me:8080/something"
		assert !d.validate()

	}
}
