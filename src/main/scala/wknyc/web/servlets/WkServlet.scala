package wknyc.web.servlets

import javax.servlet.http.{HttpSession, HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.logging.LogFactory
import scala.util.matching.Regex
import wknyc.web.WknycSession

trait WkServlet {
	protected lazy val log = LogFactory.getLog(getClass)
	lazy val htmlSuccess = "default/success.vm"
	lazy val htmlError = "default/error.vm"
	lazy val jsonSuccess = "default/jsonSuccess.vm"
	lazy val jsonError = "default/jsonError.vm"
	lazy val xmlSuccess = "default/xmlSuccess.vm"
	lazy val xmlError = "default/xmlError.vm"
	private val uriRE = new Regex("(.*?)(xml|html|json)?$","uri","format")
	implicit val default = ""
	protected case class HttpHelper(request:Request,response:Response) {
		log.info(String.format("Creating HttpHelper for URI: %s",request.getRequestURI))
		private val uriMatch = uriRE.findFirstMatchIn(request.getRequestURI).get // pretty impossible to not match this RE
		val path = uriMatch.group("uri")
		val format = uriMatch.group("format") match {
			case null => "html"
			case s:String => s
		}
		def parameter(param:String)(implicit default:String) = {
			val value = request.getParameter(param)
			if (value == default || value == null) { default }
			else { value }
		}
		val session =
			request.getSession(false) match {
				case null => None
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
		// Define success and error views depending on the format extracted from RequestURI
		lazy val (success,error) =
			format match {
				case "xml" => (xmlSuccess,xmlError)
				case "json" => (jsonSuccess,jsonError)
				case _ => (htmlSuccess,htmlError)
			}
	}
}
