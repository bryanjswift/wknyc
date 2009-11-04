package wknyc.web.servlets

import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import velocity.{VelocityView}
import wknyc.Props

class ImageServlet extends HttpServlet with FileServlet {
	override def doGet(request:Request, response:Response) = {
		val view = new VelocityView("assets/imageUpload.vm")
		view.render(Map("errors" -> Nil),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		val items = FileServlet.ServletFileUpload.getItemIterator(request)
		val files = items.filter(item => !item.isFormField)
		val path = createPath(Props("wknyc.uploads.images"))
		val assets = files.map(stream => imageFromFileItemStream(stream,path))
		val view = new VelocityView("assets/imageUpload.vm")
		view.render(Map("errors" -> Nil,"assets" -> assets),request,response)
	}
}

