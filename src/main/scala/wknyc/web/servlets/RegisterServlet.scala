package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.{VelocityHelper,VelocityView}
import wknyc.Config
import wknyc.business.UserManager
import wknyc.business.validators.UserValidator
import wknyc.model.WkCredentials

class RegisterServlet extends HttpServlet with WkServlet {
	override def doGet(request:Request, response:Response) = {
		val view = new VelocityView("register.vm")
		view.render(Map("errors" -> Nil),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		val username = getParameter(request,"username")
		val password = getParameter(request,"password")
		val department = getParameter(request,"department")
		val title = getParameter(request,"title")
		val errors = UserValidator.validateCredentials(username,password,department,title)
		val view = new VelocityView("register.vm")
		errors match {
			case Nil =>
				val user = UserManager.save(
					WkCredentials(username.get,password.get,department.getOrElse(""),title.getOrElse(""),None),Config.Admin
				)
				view.render(Map("errors" -> errors,"creds" -> user),request,response)
			case _ =>
				view.render(Map("errors" -> errors),request,response)
		}
	}
}
