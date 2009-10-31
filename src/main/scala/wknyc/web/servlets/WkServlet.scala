package wknyc.web.servlets

import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}

trait WkServlet {
	implicit val default = ""
	protected def getParameter(request:Request,param:String)(implicit default:String) = {
		val value = request.getParameter(param)
		if (value == default) None else Some(value)
	}
}
