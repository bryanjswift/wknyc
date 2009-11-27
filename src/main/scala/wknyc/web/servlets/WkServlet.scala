package wknyc.web.servlets

import javax.servlet.http.{HttpSession, HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.logging.LogFactory
import wknyc.web.WknycSession

trait WkServlet {
	protected lazy val log = LogFactory.getLog(getClass)
	lazy val htmlSuccess = "default/success.vm"
	lazy val htmlError = "default/error.vm"
	lazy val jsonSuccess = "default/jsonSuccess.vm"
	lazy val jsonError = "default/jsonError.vm"
	lazy val xmlSuccess = "default/xmlSuccess.vm"
	lazy val xmlError = "default/xmlError.vm"
	private val xmlRE = """(.*)/xml$""".r
	private val jsonRE = """(.*)/json$""".r
	implicit val default = ""
	protected case class HttpHelper(request:Request,response:Response) {
		val uri = request.getRequestURI
		log.info(String.format("Creating HttpHelper for URI: %s",uri))
		def parameter(param:String)(implicit default:String) = {
			val value = request.getParameter(param)
			if (value == default || value == null) { default }
			else { value }
		}
		val session =
			request.getSession(false) match {
				case null =>
					None
				case session:HttpSession =>
					session.getAttribute(WknycSession.Key) match {
						case null => None
						case session:WknycSession => Some(session)
					}
			}
		val user =
			session match {
				case None =>
					None
				case Some(s) =>
					Some(s.user)
			}
		// Define success and error views depending on the RequestURI
		lazy val (success,error) =
			uri match {
				case xmlRE(e) =>
					(xmlSuccess,xmlError)
				case jsonRE(e) =>
					(jsonSuccess,jsonError)
				case _ =>
					(htmlSuccess,htmlError)
			}
	}
}
