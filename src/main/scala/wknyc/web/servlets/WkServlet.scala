package wknyc.web.servlets

import javax.servlet.http.{HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.FileItemStream

trait WkServlet {
	implicit val default = ""
	protected def getParameter(request:Request)(param:String)(implicit default:String) = {
		val value = request.getParameter(param)
		if (value == default || value == null) { this.default }
		else { value }
	}
	protected def getSession(request:Request) =
		request.getSession.getAttribute(WknycSession.Key) match {
			case null => None
			case session:WknycSession => Some(session)
		}
}
