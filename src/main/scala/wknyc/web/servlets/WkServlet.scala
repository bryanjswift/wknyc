package wknyc.web.servlets

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.FileItemStream

trait WkServlet {
	implicit val default = ""
	protected def getParameter(request:Request,param:String)(implicit default:String) = {
		val value = request.getParameter(param)
		if (value == default || value == null) { None }
		else { Some(value) }
	}
}
