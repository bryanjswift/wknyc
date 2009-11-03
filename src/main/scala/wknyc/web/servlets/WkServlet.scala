package wknyc.web.servlets

import java.io.{File,FileOutputStream,InputStream,OutputStream}
import javax.servlet.http.{HttpServlet, HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.FileItemStream
import wknyc.model.{Image,LargeThumbnail}

trait WkServlet {
	implicit val default = ""
	protected def getParameter(request:Request,param:String)(implicit default:String) = {
		val value = request.getParameter(param)
		if (value == default) None else Some(value)
	}
	protected def imageFromFileItemStream(stream:FileItemStream,savePath:String) = {
		val path = savePath + stream.getName.substring(stream.getName.lastIndexOf(File.separator))
		val in = stream.openStream
		val out = new FileOutputStream(new File(path))
		try {
			writeFile(in,out,new Array[Byte](1024))
			Image(path,"","",LargeThumbnail)
		} finally {
			in.close
			out.close
		}
	}
	private def writeFile(in:InputStream,out:OutputStream,buffer:Array[Byte]) = {
		var len = in.read(buffer)
		while (len != -1) {
			out.write(buffer, 0, len)
			len = in.read(buffer)
		}
	}
}
