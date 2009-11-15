package wknyc.web.servlets

import java.io.{File,FileOutputStream}
import javax.servlet.http.{HttpServlet,HttpServletRequest => Request, HttpServletResponse => Response}
import org.apache.commons.fileupload.FileItemStream
import org.apache.commons.fileupload.util.Streams
import velocity.VelocityView
import wknyc.model.{ContentInfo,Image,ImageAsset,ImageSet,ImageSize}
import wknyc.persistence.AssetDao

class ImageServlet extends HttpServlet with FileServlet {
	override lazy val htmlSuccess = "assets/imageUpload.vm"
	val path = createRelativePath(Props("wknyc.uploads.images"))
	override def doGet(request:Request, response:Response) = {
		val http = HttpHelper(request,response)
		val view = new VelocityView(http.success)
		view.render(Map("errors" -> Nil,"uuid" -> None),request,response)
	}
	override def doPost(request:Request, response:Response) = {
		import WkPredef._
		val http = HttpHelper(request,response)
		val asset = getAssetStreaming(request)
		val uuid = http.session.flatMap(session =>
			session.user.flatMap(user => {
				val s = Config.Repository.login(user)
				using(s,new AssetDao(s,user))((dao) => {
					dao.save(asset).uuid
				})
			})
		)
		val view = new VelocityView(http.success)
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
}
