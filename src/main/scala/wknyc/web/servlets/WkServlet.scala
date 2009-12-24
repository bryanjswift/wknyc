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
		private val viewData = pathAndData(uri,format)
		val path = viewData.path
		val data = viewData.data
		log.info(String.format("Creating HttpHelper for %s with {%s} in %s",path,data,format))
		val wkSession:Option[WkSession] =
			request.getSession(false) match {
				case null => None
				case httpSession:HttpSession =>
					httpSession.getAttribute(WkSession.Key) match {
						case null => None
						case session:WkSession => Some(session)
					}
			}
		val user = wkSession match {
			case None => None
			case Some(session) => Some(session.user)
		}
		val localHtml = if (format == "html") { viewData.view } else { None }
		val localJson = if (format == "json") { viewData.view } else { None }
		val localXml = if (format == "xml") { viewData.view } else { None }
		lazy val view = format match {
			case "xml" => localXml.getOrElse(xml)
			case "json" => localJson.getOrElse(json)
			case _ => localHtml.getOrElse(html)
		}
		private def pathAndData(uri:String,format:String) = {
			val config = getServletConfig
			config.getInitParameter(uri + "/" + format) match {
				case s:String => ViewData(uri,"",Some(s))
				case null => {
					val pathData = dataRE.findFirstMatchIn(uri).get
					val path = trim(pathData.group("path"))
					val data = pathData.group("data")
					config.getInitParameter(path + "/" + format) match {
						case s:String => ViewData(path,data,Some(s))
						case null => ViewData(uri,"",None)
					}
				}
			}
		}
		private def trim(str:String) =
			if (str.last == '/') {
				str.substring(0,str.length - 1)
			} else {
				str
			}
		def parameter(param:String)(implicit default:String) = {
			val value = request.getParameter(param)
			if (value == default || value == null) { default }
			else { value }
		}
	}
}

case class ViewData(path:String, data:String, view:Option[String])
