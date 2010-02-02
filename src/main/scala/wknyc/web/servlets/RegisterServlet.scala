package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.Config
import wknyc.WkPredef._
import wknyc.business.UserManager
import wknyc.model.UserRole

class RegisterServlet extends HttpServlet with UserServlet {
	override def doGet(request:Request, response:Response) = {
		val view = new VelocityView(RegisterServlet.ViewName)
		view.render(Map("errors" -> Nil, "roles" -> UserRole.list),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		val http = HttpHelper(request,response)
		val creds = getUser(http)
		// Always a new user so encrypt the password
		val result = UserManager.save(UserManager.encryptPassword(creds),Config.Admin)
		val map = result match {
			case Success(creds,message) =>
				Map("errors" -> result.errors,"creds" -> result.payload)
			case Failure(errors,message) =>
				Map("errors" -> errors,"creds" -> creds)
		}
		val view = new VelocityView(RegisterServlet.ViewName)
		view.render(map,request,response)
	}
}

object RegisterServlet {
	val ViewName = "register.vm"
}
