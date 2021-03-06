package wknyc.web.servlets

import java.io.{File,InputStream,OutputStream}
import org.apache.commons.fileupload.{FileItem,FileItemIterator,FileItemStream}
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import wknyc.model.{Image,ImageSize}

trait FileServlet {
	protected implicit def convertIterator(it:FileItemIterator):Iterator[FileItemStream] =
		new Iterator[FileItemStream] {
			def hasNext = it.hasNext
			def next = it.next
		}
	protected def createRelativePath(path:String) = {
		val folders = path.split(java.io.File.separator)
		folders.foldLeft("")((old,current) => {
			if (old == "") {
				current
			} else {
				val path = old + java.io.File.separator + current
				val file = new java.io.File(path)
				if (!file.exists) {
					file.mkdir
				}
				path
			}
		})
	}
	protected def writeFile(in:InputStream,out:OutputStream,buffer:Array[Byte]) {
		var len = in.read(buffer)
		writeFile(in,out,buffer,len)
		out.flush
	}
	private def writeFile(in:InputStream,out:OutputStream,buffer:Array[Byte],len:Int) {
		if (len != -1) {
			out.write(buffer, 0, len)
			val length = in.read(buffer)
			writeFile(in,out,buffer,length)
		}
	}
}

object FileServlet {
	lazy val StreamingUpload = new ServletFileUpload()
}
