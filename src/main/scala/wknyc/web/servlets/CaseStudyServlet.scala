package wknyc.web.servlets

import java.util.Calendar
import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.WkPredef._
import wknyc.business.{CaseStudyManager,ClientManager}
import wknyc.model.{BasicCaseStudy,ContentInfo,DownloadableAsset}

class CaseStudyServlet extends HttpServlet with WkServlet {
	override lazy val htmlSuccess = "client/caseStudy-basic.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val study =
			if (http.parameter("id") == "") {
				None
			} else {
				CaseStudyManager.get(http.parameter("id"),http.user) match {
					case Success(study,message) => Some(study)
					case Failure(_,_) => None
				}
			}
		val view = new VelocityView(http.success)
		view.render(Map("uuid" -> None,"caseStudy" -> study,"get" -> true),request,response)
	}
	override def doPost(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val study = getCaseStudy(http)
		val result = CaseStudyManager.save(study,http.user)
		val ctx = result match {
			case Success(caseStudy,message) =>
				Map("errors" -> result.errors,"caseStudy" -> Some(caseStudy),"get" -> false)
			case Failure(errors,message) =>
				Map("errors" -> errors,"caseStudy" -> Some(study),"get" -> false)
		}
		val view = new VelocityView(http.success)
		view.render(ctx,request,response)
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
		BasicCaseStudy(
			ContentInfo.Empty,
			ClientManager.get(client),
			name,
			headline,
			description,
			launch,
			List[DownloadableAsset](),
			published,
			position
		)
	}
}
