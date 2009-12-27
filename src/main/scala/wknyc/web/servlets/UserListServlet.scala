package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.WkPredef._
import wknyc.business.UserManager

class UserListServlet extends HttpServlet with WkServlet {
	override lazy val json = "user/list.json.vm"
	override def doGet(request:Request,response:Response) {
		val http = HttpHelper(request,response)
		val list = UserManager.list
		val view = new VelocityView(http.view)
		view.render(Map("users" -> list),request,response)
	}
}
