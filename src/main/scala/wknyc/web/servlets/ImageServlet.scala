package wknyc.web.servlets

import java.io.File
import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.FileItem
import org.apache.commons.fileupload.servlet.ServletFileUpload
import velocity.{VelocityView}
import wknyc.Props

class ImageServlet extends HttpServlet with FileServlet {
	val path = createPath(Props("wknyc.uploads.images"))
	override def doGet(request:Request, response:Response) = {
		val view = new VelocityView("assets/imageUpload.vm")
		view.render(Map("errors" -> Nil),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		/*
		*/
		val multipart = ServletFileUpload.isMultipartContent(request)
		val list = FileServlet.ServletFileUpload.parseRequest(request)
		val iter = list.iterator
		while (iter.hasNext) {
			val item = iter.next.asInstanceOf[FileItem]
			if (item.isFormField) {
				// process field
			} else {
				processFile(item)
			}
		}
	 /*
		val items = FileServlet.StreamingUpload.getItemIterator(request)
		//val files = items.filter(item => !item.isFormField)
		val assets = items.map(stream => imageFromFileItemStream(stream,path))
		*/
		val view = new VelocityView("assets/imageUpload.vm")
		view.render(Map("errors" -> Nil),request,response)
	}
	private def processFile(item:FileItem) = {
		val path = createPath(Props("wknyc.uploads.images"))
		val savePath = path + File.separator + "parse-" + item.getName
		val file = new File(savePath)
		item.write(file)
	}
}
