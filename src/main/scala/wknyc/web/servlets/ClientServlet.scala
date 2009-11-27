package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.WkPredef._
import wknyc.business.ClientManager
import wknyc.model.{CaseStudy,Client,ContentInfo}

class ClientServlet extends HttpServlet with MappingServlet with WkServlet {
	override lazy val html = "client/client.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val info = view(http.path).getOrElse(ViewData(http.path,http.view,""))
		log.info(String.format("doGet for %s {%s}",info.view,info.data))
		val context = info.path match {
			case "/client/list" => Map("clients" -> ClientManager.list)
			case "/client/edit" => Map("client" -> ClientManager.get(info.data))
			case _ => Map("uuid" -> None)
		}
		val velocity = new VelocityView(info.view)
		velocity.render(context,request,response)
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
		val view = new VelocityView(http.view)
		view.render(ctx,request,response)
	}
	private def getClient(http:HttpHelper) =
		Client(
			ContentInfo.Empty,
			http.parameter("name"),
			List[CaseStudy]()
		)
}
