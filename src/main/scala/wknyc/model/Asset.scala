package wknyc.model

import scala.xml.NodeSeq

/** Trait representing the basic fields of a site asset */
sealed trait Asset extends Content {
	val title:String
	def cp(uuid:String):Asset
}
object Asset {
	val Title = "title"
}
/** Trait and class representing a file path on the server */
sealed trait FileInfo {
	def path:String
	def url:String
}
object FileInfo {
	val Path = "path"
	val Url = "url"
}
/** Trait and class representing an image file on the server */
sealed trait ImageInfo extends FileInfo {
	def alt:String
	def width:Int
	def height:Int
	def size:ImageSize
}
case class Image(path:String, url:String, alt:String, size:ImageSize) extends FileInfo with ImageInfo {
	val width = size.width
	val height = size.height
}
object Image {
	val NodeType = "wk:image"
	val Alt = "alt"
	val Width = "width"
	val Height = "height"
}
/** ImageAsset supporting 'set' */
case class ImageSet(private val images:Map[ImageSize,ImageInfo]) {
	def apply(info:ImageInfo) = new ImageSet(images + (info.size -> info))
	def apply(size:ImageSize) = if (images contains size) { Some(images(size)) } else { None }
	def foreach(fcn:(ImageInfo) => Unit) = images.values.foreach(fcn)
}
object ImageSet {
	def apply(info:ImageInfo):ImageSet = ImageSet(Map(info.size -> info))
	def apply(images:ImageInfo*):ImageSet = apply(images.elements)
	def apply(images:Iterator[ImageInfo]):ImageSet = ImageSet(images.foldLeft(Map[ImageSize,ImageInfo]())((map,image) => map + (image.size -> image)))
}
// Image related asset classes
case class ImageAsset(contentInfo:ContentInfo, title:String, images:ImageSet) extends Asset {
	def cp(uuid:String) = ImageAsset(contentInfo.cp(uuid), title, images)
}
object ImageAsset {
	val NodeType = "wk:imageAsset"
}
// Copy related asset classes
case class CopyAsset(contentInfo:ContentInfo, title:String, body:NodeSeq) extends Asset {
	def cp(uuid:String) = CopyAsset(contentInfo.cp(uuid), title, body)
}
object CopyAsset {
	val NodeType = "wk:copyAsset"
	val Body = "body"
}
// Download related asset classes (video, audio, archive, document)
case class DownloadableAsset(contentInfo:ContentInfo, title:String, path:String, url:String) extends Asset with FileInfo {
	def cp(uuid:String) = DownloadableAsset(contentInfo.cp(uuid), title, url, path)
}
object DownloadableAsset {
	val NodeType = "wk:downloadAsset"
}
// Press (link to press) asset
case class PressAsset(contentInfo:ContentInfo, title:String, author:String, source:String, sourceName:String) extends Asset {
	def cp(uuid:String) = PressAsset(contentInfo.cp(uuid), title, author, source, sourceName)
}
object PressAsset {
	val NodeType = "wk:pressAsset"
	val Author = "author"
	val Source = "source"
	val SourceName = "sourceName"
}
// Award (Info about awards) asset
case class AwardAsset(contentInfo:ContentInfo, title:String, source:String, description:CopyAsset, image:ImageAsset) extends Asset {
	def cp(uuid:String) = AwardAsset(contentInfo.cp(uuid), title, source, description, image)
}
object AwardAsset {
	val NodeType = "wk:awardAsset"
	val Source = "source"
	val Description = "description"
	val Image = "image"
}
