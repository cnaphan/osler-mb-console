import surveymgr.*

class SessionTagLib {
	
	def messages = {
		if (flash.message || flash.error || (flash.messages?.size() > 0) || request.errors) {
			out << render(template: "/utils/messages")
			flash.message = null
			flash.messages = null
			flash.error = null
		}
	}
}

