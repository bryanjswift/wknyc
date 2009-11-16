package wknyc.web.servlets

import java.util.Calendar
import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.business.CaseStudyManager
import wknyc.model.{BasicCaseStudy,ContentInfo,DownloadableAsset}

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
		// TODO: not returning a UUID anymore, returning a Some(wknyc.business.Response)
		// Maybe make an empty ContentInfo instance which can be used as a placeholder and
		// the save method of CaseStudyManager populates ContentInfo if the user exists
		// otherwise it returns a Failure Response.
		val uuid = http.user.flatMap(user => {
			val casestudy = CaseStudyManager.save(getCaseStudy(http).get,user)
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
		val launch = Calendar.getInstance
		launch.set(year.toInt,month.toInt,1)
		val published = param("displayOnSiteRadio") == "true"
		val position = 0
		http.user.flatMap(user => Some(
			BasicCaseStudy(
				ContentInfo(user),
				name,
				headline,
				description,
				launch,
				List[DownloadableAsset](),
				published,
				position
		)))
	}
}
