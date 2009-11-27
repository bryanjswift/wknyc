package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.business.UserManager
import wknyc.model.User._
import wknyc.web.WknycSession

class LoginServlet extends HttpServlet with WkServlet {
	override lazy val html = "login.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val view = new VelocityView(http.view)
		view.render(Map("user" -> null,"errors" -> Nil),request,response)
	}
	override def doPost(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val param = http.parameter(_)
		val (context,user) = UserManager.authenticate(param("username"),param("password")) match {
			case Some(user) =>
				val session = request.getSession(true)
				val opt = Some(user)
				session.setAttribute(WknycSession.Key,WknycSession(user))
				(Map("user" -> opt,"errors" -> Nil),opt)
			case None =>
				(Map("user" -> None,"errors" -> List("bad login")),None)
		}
		val view = new VelocityView(http.view)
		view.render(context,request,response)
	}
}

