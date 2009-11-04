package wknyc.web.servlets

import java.io.File
import org.apache.commons.fileupload.{FileItemIterator,FileItemStream}
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload

trait FileServlet extends WkServlet {
	protected implicit def convertIterator(it:FileItemIterator):Iterator[FileItemStream] =
		new Iterator[FileItemStream] {
			def hasNext = it.hasNext
			def next = it.next
		}
	protected def createPath(path:String) = {
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
}

object FileServlet {
	lazy val DiskFileItemFactory = new DiskFileItemFactory(2048,new File("target/temp"))
	lazy val ServletFileUpload = new ServletFileUpload(DiskFileItemFactory)
	lazy val StreamingUpload = new ServletFileUpload()
}
