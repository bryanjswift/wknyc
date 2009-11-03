package wknyc.web.servlets

import java.io.File
import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.FileItem
import wknyc.model.{Image,LargeThumbnail}

trait WkServlet {
	implicit val default = ""
	protected def getParameter(request:Request,param:String)(implicit default:String) = {
		val value = request.getParameter(param)
		if (value == default) None else Some(value)
	}
	protected def assetFromFileItem(item:FileItem,savePath:String) = {
		val name = item.getName.substring(item.getName.lastIndexOf(File.separator))
		val path = savePath + name
		val file = new File(path)
		item.write(file)
		Image(path,"","",LargeThumbnail)
	}
}
