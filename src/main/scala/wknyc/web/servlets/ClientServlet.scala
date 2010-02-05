package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.WkPredef._
import wknyc.business.ClientManager
import wknyc.model.{Client,ContentInfo}

class ClientServlet extends HttpServlet with WkServlet {
	override lazy val html = "client/client.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val velocity = new VelocityView(http.view)
		velocity.render(Map("uuid" -> None),request,response)
	}
	override def doPost(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val client = Client(ContentInfo.Empty,http.parameter("name"),Nil)
		val result = ClientManager.save(client,http.user)
		val ctx = result match {
			case Success(payload,message) =>
				log.info(String.format("Client saved. UUID: %s",payload.uuid))
				Map("errors" -> result.errors,"client" -> Some(payload),"get" -> false,"uuid" -> payload.uuid)
			case Failure(errors,message) =>
				log.info(String.format("Client failed to save. errors: %s",errors.toString))
				Map("errors" -> errors,"client" -> Some(client),"get" -> false,"uuid" -> client.uuid)
		}
		val view = new VelocityView(http.view)
		view.render(ctx,request,response)
	}
}
