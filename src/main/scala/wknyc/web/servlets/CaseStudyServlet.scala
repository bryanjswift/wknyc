package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.model.{CaseStudy,ContentInfo}
import wknyc.persistence.ClientDao

@WebServlet{ val name = "CaseStudy" , val urlPatterns = Array("/client/casestudy/*") }
class CaseStudyServlet extends HttpServlet with WkServlet {
	override lazy val htmlSuccess = "client/caseStudy-basic.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val view = new VelocityView(http.success)
		view.render(Map("uuid" -> None),request,response)
	}
	override def doPost(request:Request, response:Response) {
		import WkPredef._
		val http = HttpHelper(request,response)
		val uuid = http.user.flatMap(user => {
			val session = Config.Repository.login(user)
			val casestudy = using(session,new ClientDao(session,user))(_.save(getCaseStudy(http).get).uuid)
			Some(casestudy)
		})
		val view = new VelocityView(http.success)
		view.render(Map("uuid" -> uuid),request,response)
	}
	private def getCaseStudy(http:HttpHelper) = {
		val param = http.parameter(_)
		val name = param("nameField")
		val headline = param("shortDescField")
		val description = param("longDescField")
		val client = param("clientSelect")
		val order = param("orderpicker")
		val month = param("launchDateMonth")
		val year = param("launchDateYear")
		val display = param("displayOnSiteRadio")
		http.user.flatMap(user => Some(CaseStudy(ContentInfo(user),name,headline,description)))
	}
}
