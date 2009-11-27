package wknyc.web.servlets

import javax.servlet.http.{HttpSession, HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.logging.LogFactory
import scala.util.matching.Regex
import wknyc.web.WknycSession

trait WkServlet {
	protected lazy val log = LogFactory.getLog(getClass)
	lazy val html = "default/html.vm"
	lazy val json = "default/json.vm"
	lazy val xml = "default/xml.vm"
	private val uriRE = new Regex("(.*?)(xml|html|json)?$","uri","format")
	implicit val default = ""
	protected case class HttpHelper(request:Request,response:Response) {
		private val uriMatch = uriRE.findFirstMatchIn(request.getRequestURI).get // pretty impossible to not match this RE
		val path = uriMatch.group("uri")
		val format = uriMatch.group("format") match {
			case null => "html"
			case s:String => s
		}
		log.info(String.format("Creating HttpHelper for URI: %s with format %s",path,format))
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
		lazy val view =
			format match {
				case "xml" => xml
				case "json" => json
				case _ => html
			}
	}
}
