package wknyc.web.servlets

import java.util.Calendar
import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.WkPredef._
import wknyc.business.{CaseStudyManager,ClientManager}
import wknyc.model.{CaseStudy,CaseStudyStatus,ContentInfo}

class CaseStudyServlet extends HttpServlet with WkServlet {
	override lazy val html = "client/caseStudy-basic.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val study =
			CaseStudyManager.get(http.parameter("id"),http.user) match {
				case Success(caseStudy,message) => Some(caseStudy)
				case Failure(_,_) => None
			}
		val view = new VelocityView(http.view)
		val clients = ClientManager.list
		view.render(Map("uuid" -> None,"caseStudy" -> study,"get" -> true,"clients" -> clients,"status"->CaseStudyStatus.list),request,response)
	}
	override def doPost(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val study = getCaseStudy(http)
		val result = CaseStudyManager.save(study,http.user)
		var ctx:Map[String,Any] = result match { // need type declaration because otherwise it calls it Map[String,Product]
			case Success(caseStudy,message) =>
				Map("errors" -> result.errors,"caseStudy" -> Some(caseStudy))
			case Failure(errors,message) =>
				Map("errors" -> errors,"caseStudy" -> Some(study))
		}
		ctx = ctx ++ Map("get" -> false,"clients" -> ClientManager.list,"status" -> CaseStudyStatus.list)
		val view = new VelocityView(http.view)
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
		val status = param("status")
		val position = 0
		CaseStudy(
			ContentInfo.Empty,
			ClientManager.get(client),
			name,
			launch,
			headline,
			description,
			Nil,
			CaseStudyStatus(status.toLong),
			position
		)
	}
}
