package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.{VelocityHelper,VelocityView}
import wknyc.Config
import wknyc.WkPredef._
import wknyc.business.UserManager
import wknyc.model.User._
import wknyc.model.{ContentInfo,Employee,PersonalInfo,UserRole,WkCredentials}

class EditUserServlet extends HttpServlet with WkServlet {
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val employee = UserManager.get(http.data)
		val context = Map("user" -> employee)
		val velocity = new VelocityView(http.view)
		velocity.render(context,request,response)
	}
}
