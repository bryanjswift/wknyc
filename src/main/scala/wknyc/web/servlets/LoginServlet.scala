package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.business.UserManager
import wknyc.model.User._
import wknyc.web.WknycSession

class LoginServlet extends HttpServlet with WkServlet {
	override def doGet(request:Request, response:Response) = {
		val view = new VelocityView(LoginServlet.ViewName)
		view.render(Map("user" -> null,"errors" -> Nil),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		val username = getParameter(request)("username")
		val password = getParameter(request)("password")
		val (context,user) = UserManager.authenticate(username,password) match {
			case Some(user) =>
				val opt = Some(user)
				(Map("user" -> opt,"errors" -> Nil),opt)
			case None =>
				(Map("user" -> None,"errors" -> List("bad login")),None)
		}
		val session = request.getSession(true)
		session.setAttribute(WknycSession.Key,WknycSession(user))
		val view = new VelocityView(LoginServlet.ViewName)
		view.render(context,request,response)
	}
}

object LoginServlet {
	val ViewName = "login.vm"
}
