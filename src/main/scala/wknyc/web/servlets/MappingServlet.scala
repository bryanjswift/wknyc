package wknyc.web.servlets

import javax.servlet.ServletConfig
import javax.servlet.Servlet

trait MappingServlet {
	self: Servlet =>
	private val trimRE = "/$".r
	def view(path:String) = {
		val config = getServletConfig
		// TODO: in 2.8 changes to just Option(getInitiParameter(path))
		config.getInitParameter(trimRE.replaceFirstIn(path,"")) match {
			case null => None
			case s:String => Some(s)
		}
	}
}

