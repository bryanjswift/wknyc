package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.{VelocityHelper,VelocityView}
import wknyc.Config
import wknyc.WkPredef._
import wknyc.business.UserManager
import wknyc.model.User._
import wknyc.model.{ContentInfo,Employee,PersonalInfo,UserRole,WkCredentials}

class RegisterServlet extends HttpServlet with WkServlet {
	override def doGet(request:Request, response:Response) = {
		val view = new VelocityView(RegisterServlet.ViewName)
		view.render(Map("errors" -> Nil, "roles" -> UserRole.list),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		val http = HttpHelper(request,response)
		val creds = getUser(http)
		val result = UserManager.save(creds,Config.Admin)
		val map = result match {
			case Success(creds,message) =>
				Map("errors" -> result.errors,"creds" -> result.payload)
			case Failure(errors,message) =>
				Map("errors" -> errors,"creds" -> creds)
		}
		val view = new VelocityView(RegisterServlet.ViewName)
		view.render(map,request,response)
	}
	private def getCredentials(http:HttpHelper) =
		WkCredentials(http.parameter(Username),http.parameter(Password),UserRole(http.parameter(Role)),http.parameter(Title),None)
	private def getPersonalInfo(http:HttpHelper) =
		PersonalInfo(http.parameter(Employee.FirstName),http.parameter(Employee.LastName),Nil)
	private def getEmployee(http:HttpHelper) =
		Employee(ContentInfo.Empty,getCredentials(http),getPersonalInfo(http))
	private def getUser(http:HttpHelper) = getEmployee(http)
}

object RegisterServlet {
	val ViewName = "register.vm"
}
