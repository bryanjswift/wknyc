package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.{VelocityHelper,VelocityView}
import wknyc.Config
import wknyc.business.UserManager
import wknyc.business.validators.UserValidator
import wknyc.model.User._
import wknyc.model.WkCredentials

class RegisterServlet extends HttpServlet with WkServlet {
	override def doGet(request:Request, response:Response) = {
		val view = new VelocityView(RegisterServlet.ViewName)
		view.render(Map("errors" -> Nil),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		val param = getParameter(request)(_)
		val creds = WkCredentials(param(Username),param(Password),param(Department),param(Title),None)
		// Validation should happen in UserManager
		val errors = UserValidator.validateCredentials(creds)
		val view = new VelocityView(RegisterServlet.ViewName)
		errors match {
			case Nil =>
				val user = UserManager.register(creds,Config.Admin)
				view.render(Map("errors" -> errors,"creds" -> user),request,response)
			case _ =>
				view.render(Map("errors" -> errors,"creds" -> None),request,response)
		}
	}
}

object RegisterServlet {
	val ViewName = "register.vm"
}
