package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.WkPredef._
import wknyc.business.ClientManager
import wknyc.model.{CaseStudy,Client,ContentInfo}

class ClientServlet extends HttpServlet with WkServlet {
	override lazy val htmlSuccess = "client/client.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val (view,context) = http.path match {
			case "/client/list" => (new VelocityView("client/client-list.vm"),Map("clients" -> ClientManager.list))
			case _ => (new VelocityView(http.success),Map("uuid" -> None))
		}
		view.render(context,request,response)
	}
	override def doPost(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val client = getClient(http)
		val result = ClientManager.save(client,http.user)
		val ctx = result match {
			case Success(payload,message) =>
				Map("errors" -> result.errors,"client" -> Some(payload),"get" -> false,"uuid" -> payload.uuid)
			case Failure(errors,message) =>
				Map("errors" -> errors,"client" -> Some(client),"get" -> false,"uuid" -> client.uuid)
		}
		val view = new VelocityView(http.success)
		view.render(ctx,request,response)
	}
	private def getClient(http:HttpHelper) =
		Client(
			ContentInfo.Empty,
			http.parameter("name"),
			List[CaseStudy]()
		)
}
