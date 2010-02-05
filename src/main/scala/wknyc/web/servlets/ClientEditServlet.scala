package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.VelocityView
import wknyc.WkPredef._
import wknyc.business.ClientManager
import wknyc.model.{Client,ContentInfo}

class ClientEditServlet extends HttpServlet with WkServlet {
	override lazy val html = "client/client-edit.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val context = Map("errors" -> Nil,"client" -> Some(ClientManager.get(http.data).payload),"uuid" -> Some(http.data))
		val velocity = new VelocityView(http.view)
		velocity.render(context,request,response)
	}
	override def doPost(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val result = ClientManager.get(http.parameter("uuid"))
		val context =
			result match {
				case Failure(errors,message) =>
					log.info(String.format("Unable to retrieve Client for UUID: %s",http.parameter("uuid")))
					Map("errors" -> errors,"client" -> None,"uuid" -> Some(http.parameter("uuid")))
				case Success(c,message) =>
					log.info(String.format("Retrieved Client for UUID: %s",http.parameter("uuid")))
					val client = Client(c.contentInfo,http.parameter("name"),c.caseStudies)
					save(client,http)
			}
		val velocity = new VelocityView(http.view)
		velocity.render(context,request,response)
	}
	private def save(client:Client,http:HttpHelper) = {
		val result = ClientManager.save(client,http.user)
		result match {
			case Success(payload,message) =>
				log.info(String.format("Client saved. UUID: %s",payload.uuid))
				Map("errors" -> result.errors,"client" -> Some(payload),"uuid" -> payload.uuid)
			case Failure(errors,message) =>
				log.info(String.format("Client failed to save. errors: %s",errors.toString))
				Map("errors" -> errors,"client" -> Some(client),"uuid" -> Some(http.parameter("uuid")))
		}
	}
}

