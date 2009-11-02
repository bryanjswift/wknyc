package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import scala.collection.jcl.Conversions.convertList
import velocity.{VelocityHelper,VelocityView}
import wknyc.Config

class FileServlet extends HttpServlet with WkServlet {
	override def doPost(request:Request, response:Response) = {
		val items = FileServlet.ServletFileUpload.parseRequest(request)
		val view = new VelocityView("assets/imageUpload.vm")
		view.render(Map("errors" -> Nil),request,response)
	}
}

object FileServlet {
	lazy val FileFactory = new DiskFileItemFactory
	lazy val ServletFileUpload = new ServletFileUpload(FileFactory)
}