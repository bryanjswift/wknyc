package wknyc.web.servlets

import javax.servlet.http.{HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.FileItemStream

trait WkServlet {
	lazy val htmlSuccess = "default/success.vm"
	lazy val htmlError = "default/error.vm"
	lazy val jsonSuccess = "default/jsonSuccess.vm"
	lazy val jsonError = "default/jsonError.vm"
	lazy val xmlSuccess = "default/xmlSuccess.vm"
	lazy val xmlError = "default/xmlError.vm"
	private val xmlRE = """(.*)xml$""".r
	private val jsonRE = """(.*)json$""".r
	protected case class HttpHelper(request:Request,response:Response) {
		implicit val default = ""
		def parameter(param:String) = {
			val value = request.getParameter(param)
			if (value == default || value == null) { this.default }
			else { value }
		}
		val session =
			request.getSession.getAttribute(WknycSession.Key) match {
				case null => None
				case session:WknycSession => Some(session)
			}
		lazy val (success,error) =
			request.getRequestURI match {
				case xmlRE(e) =>
					(xmlSuccess,xmlError)
				case jsonRE(e) =>
					(jsonSuccess,jsonError)
				case _ =>
					(htmlSuccess,htmlError)
			}
	}
}
