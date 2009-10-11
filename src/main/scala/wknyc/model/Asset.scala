package wknyc.model

import java.util.Date
import scala.xml.NodeSeq

/** Trait representing the basic fields of a site asset */
sealed trait Asset extends Content {
	val title:String
}
/** Trait and class representing a file path on the server */
trait FileInfo {
	def path:String
	def url:String
}
class File(val path:String, val url:String) extends FileInfo
/** Size of image trait and related objects */
sealed trait ImageSize { def name:String }
case object LargeThumbnail extends ImageSize { val name = "Large" }
case object MediumThumbnail extends ImageSize { val name = "Medium" }
case object SmallThumbnail extends ImageSize { val name = "Small" }
case object TinyThumbnail extends ImageSize { val name = "Tiny" }
/** Trait and class representing an image file on the server */
sealed trait ImageInfo {
	def alt:String
	def width:Int
	def height:Int
	def size:ImageSize
}
class Image(path:String, url:String, val alt:String, val width:Int, val height:Int, val size:ImageSize) extends File(path,url) with ImageInfo
object Image {
	val NodeType = "wk:image"
}
/** ImageAsset supporting 'set' */
class ImageSet(private val images:Map[ImageSize,ImageInfo]) {
	def this(info:ImageInfo) = this(Map(info.size -> info))
	def this(images:ImageInfo*) = this(images.foldLeft(Map[ImageSize,ImageInfo]())((map,image) => map + (image.size -> image)))
	def apply(info:ImageInfo) = new ImageSet(images + (info.size -> info))
	def apply(size:ImageSize) = if (images contains size) { Some(images(size)) } else { None }
	def foreach(fcn:(ImageInfo) => Unit) = images.values.foreach(fcn)
}
// Image related asset classes
case class ImageAsset(val contentInfo:ContentInfo, val title:String, val images:ImageSet) extends Asset {
	def cp(uuid:String) = ImageAsset(contentInfo.cp(uuid), title, images)
}
object ImageAsset {
	val NodeType = "wk:imageAsset"
}
// Copy related asset classes
case class CopyAsset(val contentInfo:ContentInfo, val title:String, val body:NodeSeq) extends Asset {
	def cp(uuid:String) = CopyAsset(contentInfo.cp(uuid), title, body)
}
object CopyAsset {
	val NodeType = "wk:copyAsset"
}
// Download related asset classes (video, audio, archive, document)
case class DownloadableAsset(val contentInfo:ContentInfo, val title:String, val url:String, val path:String) extends Asset with FileInfo {
	def cp(uuid:String) = DownloadableAsset(contentInfo.cp(uuid), title, url, path)
}
object DownloadableAsset {
	val NodeType = "wk:downloadAsset"
}
// Press (link to press) asset
case class PressAsset(val contentInfo:ContentInfo, val title:String, val author:String, val source:String, val sourceName:String) extends Asset {
	def cp(uuid:String) = PressAsset(contentInfo.cp(uuid), title, author, source, sourceName)
}
object PressAsset {
	val NodeType = "wk:pressAsset"
}
// Award (Info about awards) asset
case class AwardAsset(val contentInfo:ContentInfo, val title:String, val source:String, val description:CopyAsset, val image:ImageAsset) extends Asset {
	def cp(uuid:String) = AwardAsset(contentInfo.cp(uuid), title, source, description, image)
}
object AwardAsset {
	val NodeType = "wk:awardAsset"
}
