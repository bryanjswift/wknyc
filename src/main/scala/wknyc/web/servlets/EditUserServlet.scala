package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.{VelocityHelper,VelocityView}
import wknyc.Config
import wknyc.WkPredef._
import wknyc.business.UserManager
import wknyc.model.User._
import wknyc.model.{Employee,WkCredentials}

class EditUserServlet extends HttpServlet with UserServlet {
	override lazy val html = "user/edit.html.vm"
	override def doGet(request:Request, response:Response) {
		val http = HttpHelper(request,response)
		val employee = UserManager.get(http.data).payload
		val context = Map("errors" -> Nil,"user" -> Some(employee),"uuid" -> Some(http.data))
		val velocity = new VelocityView(http.view)
		velocity.render(context,request,response)
	}
	override def doPost(request:Request,response:Response) {
		val http = HttpHelper(request,response)
		val result = UserManager.get(http.parameter("uuid"))
		val context =
			result match {
				case Failure(errors,message) =>
					log.info(String.format("Unable to retrieve User for UUID: %s",http.parameter("uuid")))
					Map("errors" -> errors,"user" -> None,"uuid" -> Some(http.parameter("uuid")))
				case Success(user,message) =>
					log.info(String.format("Retrieved User for UUID: %s",http.parameter("uuid")))
					save(user.asInstanceOf[Employee],http)
			}
		val velocity = new VelocityView(http.view)
		velocity.render(context,request,response)
	}
	private def save(user:Employee,http:HttpHelper) = {
		val employee = getUser(http)
		val toSave =
			if (http.hasParameter("password")) {
				UserManager.encryptPassword(Employee(user.contentInfo,employee.credentials,employee.personalInfo))
			} else {
				val creds = employee.credentials
				// password in user is already encrypted
				Employee(
					user.contentInfo,
					WkCredentials(creds.username,user.password,creds.role,creds.title,creds.uuid),
					employee.personalInfo
				)
			}
		val result = UserManager.save(toSave,http.user)
		result match {
			case Success(payload,message) =>
				log.info(String.format("User saved. UUID: %s",payload.uuid))
				Map("errors" -> result.errors,"user" -> Some(result.payload),"uuid" -> payload.uuid)
			case Failure(errors,message) =>
				log.info(String.format("User failed to save. errors: %s",errors.toString))
				Map("errors" -> errors,"user" -> Some(employee),"uuid" -> Some(http.parameter("uuid")))
		}
	}
}
