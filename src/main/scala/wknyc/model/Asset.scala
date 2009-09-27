package wknyc.model

import java.util.Date

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
sealed trait ImageSize
case object LargeThumbnail extends ImageSize
case object MediumThumbnail extends ImageSize
case object SmallThumbnail extends ImageSize
case object TinyThumbnail extends ImageSize
/** Trait and class representing an image file on the server */
sealed trait ImageInfo {
	def alt:String
	def width:Int
	def height:Int
	def size:ImageSize
}
class Image(path:String, url:String, val alt:String, val width:Int, val height:Int, val size:ImageSize) extends File(path,url) with ImageInfo
/** ImageAsset and supporting classes */
class ImageSet(val large:ImageInfo, val medium:ImageInfo, val small:ImageInfo, val tiny:ImageInfo)
case class ImageAsset(val contentInfo:ContentInfo, val title:String, val images:ImageSet) extends Asset
// Copy related asset classes
case class CopyAsset(val contentInfo:ContentInfo, val title:String, val body:String) extends Asset
// Download related asset classes (video, audio, archive, document)
case class DownloadableAsset(val contentInfo:ContentInfo, val title:String, val url:String, val path:String) extends Asset with FileInfo
// Press (link to press) asset
case class PressAsset(val contentInfo:ContentInfo, val title:String, val author:String, val source:String, val sourceName:String) extends Asset
