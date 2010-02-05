package wknyc.web.servlets

import javax.servlet.Servlet
import javax.servlet.http.{HttpSession, HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.logging.LogFactory
import scala.util.matching.Regex
import wknyc.web.WkSession

trait WkServlet {
	self: Servlet =>
	protected lazy val log = LogFactory.getLog(getClass)
	lazy val html = "default/html.vm"
	lazy val json = "default/json.vm"
	lazy val xml = "default/xml.vm"
	private val uriRE = new Regex("(.*?)(xml|html|json)?$","uri","format")
	private val dataRE = new Regex("(.*?)/([^/]*)$","path","data")
	implicit val default = ""
	protected case class HttpHelper(request:Request,response:Response) {
		// pretty impossible to not match this RE
		private val uriMatch = uriRE.findFirstMatchIn(request.getRequestURI).get
		private val uri = trim(uriMatch.group("uri"))
		private val format = uriMatch.group("format") match {
			case null => "html"
			case s:String => s
		}
		private val servletPath = request.getServletPath
		private val pathData =
			if (servletPath == uri) {
				PathAndData(uri,"")
			} else {
				PathAndData(servletPath,uri.replace(servletPath + "/",""))
			}
		val path = pathData.path
		val data = pathData.data
		log.info(String.format("Creating HttpHelper for %s with {%s} in %s",path,data,format))
		val session:Option[WkSession] =
			request.getSession(false) match {
				case null => None
				case httpSession:HttpSession =>
					httpSession.getAttribute(WkSession.Key) match {
						case null => None
						case session:WkSession => Some(session)
					}
			}
		val user = session match {
			case None => None
			case Some(wkSession) => Some(wkSession.user)
		}
		lazy val view = format match {
			case "xml" => xml
			case "json" => json
			case _ => html
		}
		private def trim(str:String) =
			if (str.length > 0 && str.last == '/') {
				str.substring(0,str.length - 1)
			} else {
				str
			}
		def hasParameter(param:String) = request.getParameter(param) != null
		def parameter(param:String)(implicit default:String) = {
			val value = request.getParameter(param)
			log.info(String.format("get parameter %s ... found %s",param,value))
			if (value == default || value == null) { default }
			else { value }
		}
	}
}

case class PathAndData(path:String, data:String)
