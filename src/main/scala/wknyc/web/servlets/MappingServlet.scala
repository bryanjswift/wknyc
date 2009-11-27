package wknyc.web.servlets

import javax.servlet.ServletConfig
import javax.servlet.Servlet

trait MappingServlet {
	self: Servlet =>
	private val trimRE = "/$".r
	private val dataRE = "(.*?)/([^/]*)$".r
	def view(path:String) =
		getServletConfig.getInitParameter(trimRE.replaceFirstIn(path,"")) match {
			case null => viewAndData(path)
			case s:String => Some(ViewData(trimRE.replaceFirstIn(path,""),s,""))
		}
	private def viewAndData(s:String) = {
		val target = trimRE.replaceFirstIn(s,"")
		val m = dataRE.findFirstMatchIn(target).get
		val path = m.group(1)
		val data = m.group(2)
		getServletConfig.getInitParameter(trimRE.replaceFirstIn(path,"")) match {
			case null => None
			case s:String => Some(ViewData(path,s,data))
		}
	}
}

case class ViewData(path:String,view:String, data:String)
