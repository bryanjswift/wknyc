package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.WkPredef._
import wknyc.business.CaseStudyManager

class CaseStudyListServlet extends HttpServlet with WkServlet {
	override lazy val json = "casestudy/list.json.vm"
	override def doGet(request:Request,response:Response) {
		val http = HttpHelper(request,response)
		val list = CaseStudyManager.list
		val view = new VelocityView(http.view)
		view.render(Map("studies" -> list),request,response)
	}
}
