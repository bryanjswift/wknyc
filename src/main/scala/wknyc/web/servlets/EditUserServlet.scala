package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.{VelocityHelper,VelocityView}
import wknyc.Config
import wknyc.WkPredef._
import wknyc.business.UserManager
import wknyc.model.User._

class EditUserServlet extends HttpServlet with UserServlet {
	override lazy val html = "user/edit.html.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val employee = UserManager.get(http.data)
		val context = Map("uuid" -> http.data)
		val velocity = new VelocityView(http.view)
		velocity.render(context,request,response)
	}
	override def doPost(request:Request,response:Response) {
		val http = HttpHelper(request,response)
		// retrieve based on uuid
		val uuid = http.parameter("uuid")
		val user = UserManager.get(uuid)
		val employee = getUser(http)
		// copy fields, only copy password if it's set in form
		// save new User instance
		// post success message
		true
	}
}
