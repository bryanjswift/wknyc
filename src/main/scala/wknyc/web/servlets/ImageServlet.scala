package wknyc.web.servlets

import java.io.{File,FileOutputStream}
import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.{FileItem,FileItemStream}
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.util.Streams
import velocity.{VelocityView}
import wknyc.model.{ContentInfo,Image,ImageAsset,ImageSet,ImageSize}

class ImageServlet extends HttpServlet with FileServlet {
	val path = createPath(Props("wknyc.uploads.images"))
	override def doGet(request:Request, response:Response) = {
		val view = new VelocityView("assets/imageUpload.vm")
		view.render(Map("errors" -> Nil),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		val asset = getAssetStreaming(request)
		val view = new VelocityView("assets/imageUpload.vm")
		view.render(Map("errors" -> Nil),request,response)
	}
	private def getAssetTempFiles(request:Request) = {
		val list = FileServlet.ServletFileUpload.parseRequest(request).asInstanceOf[java.util.List[FileItem]]
		val (formFields, fileItems) = list.iterator.duplicate // implicitly converted from java.util.Iterator to Iterator
		val name = formFields.find(item => item.getFieldName == "name").get.getString
		val images = for {
			item <- fileItems
			if (!item.isFormField)
		} yield processFile(item)
		ImageAsset(ContentInfo(Config.Admin),name,ImageSet(images))
	}
	private def getAssetStreaming(request:Request) = {
		val (formFields, fileItems) = FileServlet.StreamingUpload.getItemIterator(request).duplicate
		// getting name requires work
		val name = Streams.asString(formFields.find(item => item.getFieldName == "name").get.openStream)
		var images:List[Image] = Nil
		while (fileItems.hasNext) {
			val stream = fileItems.next
			if (!stream.isFormField) {
				images = imageFromFileItemStream(stream,path) :: images
			}
		}
		ImageAsset(ContentInfo(Config.Admin),name,ImageSet(images.elements))
	}
	private def imageFromFileItemStream(stream:FileItemStream,savePath:String) = {
		val path = savePath + File.separator + stream.getName
		val in = stream.openStream
		val out = new FileOutputStream(new File(path))
		try {
			writeFile(in,out,new Array[Byte](1024))
			Image(path,"","",ImageSize(stream.getFieldName))
		} finally {
			in.close
			out.close
		}
	}
	private def processFile(item:FileItem) = {
		val path = createPath(Props("wknyc.uploads.images"))
		val size = ImageSize(item.getFieldName)
		val savePath = path + File.separator + item.getName
		val file = new File(savePath)
		item.write(file)
		Image(savePath,"","",size)
	}
}
