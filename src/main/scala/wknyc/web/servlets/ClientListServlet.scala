package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.WkPredef._
import wknyc.business.ClientManager

class ClientListServlet extends HttpServlet with WkServlet {
	override lazy val html = "client/client-list.vm"
	override lazy val json = "client/client-list.json.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val list = ClientManager.list
		val view = new VelocityView(http.view)
		view.render(Map("clients" -> list),request,response)
	}
}

