package wknyc.web.servlets

import java.io.{File,FileOutputStream}
import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.FileItemStream
import org.apache.commons.fileupload.util.Streams
import velocity.VelocityView
import wknyc.business.Manager
import wknyc.model.{ContentInfo,Image,ImageAsset,ImageSet,ImageSize}
import wknyc.persistence.AssetDao

// TODO: Should not mixin Manager
class ImageServlet extends HttpServlet with FileServlet with WkServlet with Manager {
	override lazy val html = "assets/imageUpload.vm"
	val path = createRelativePath(Props("wknyc.uploads.images"))
	override def doGet(request:Request, response:Response) = {
		val http = HttpHelper(request,response)
		val view = new VelocityView(http.view)
		view.render(Map("errors" -> Nil,"uuid" -> None),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		import WkPredef._
		val http = HttpHelper(request,response)
		val asset = getAssetStreaming(request)
		val uuid:Option[String] = http.user.flatMap(user => {
				using(new AssetDao(user))((dao) => {
					dao.save(asset).uuid
				})
			})
		val view = new VelocityView(http.view)
		view.render(Map("errors" -> Nil,"uuid" -> uuid),request,response)
	}
	private def getAssetStreaming(request:Request) = {
		val (formFields, fileItems) = FileServlet.StreamingUpload.getItemIterator(request).duplicate
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
		val file = new File(path)
		if (!file.exists) file.createNewFile
		val in = stream.openStream
		val out = new FileOutputStream(file)
		try {
			writeFile(in,out,new Array[Byte](1024))
			Image(path,"","",ImageSize(stream.getFieldName))
		} finally {
			in.close
			out.close
		}
	}
}
