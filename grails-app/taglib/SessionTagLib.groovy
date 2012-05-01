import surveymgr.*

class SessionTagLib {
	
	def messages = {						
		out << render(template: "/utils/messages")
		flash.message = null
		flash.messages = null
		flash.error = null
		
	}
}

