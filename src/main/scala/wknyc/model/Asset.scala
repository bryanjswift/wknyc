package wknyc.model

import java.util.Date

/** Trait representing the basic fields of a site asset */
sealed trait Asset extends Content {
	val title:String
}
/** Trait representing the basic fields of a site asset which is treated as a file */
sealed trait FileAsset {
	val path:String
	val url:String
}
/** Trait representing the basic fields of a site asset which is an image */
sealed trait ImageAsset extends FileAsset {
	val alt:String
	val width:Int
	val height:Int
	val size:ImageSize
}
/** Size of image trait and related classes */
sealed trait ImageSize
case class LargeThumbnail() extends ImageSize
case class MediumThumbnail() extends ImageSize
case class SmallThumbnail() extends ImageSize
case class TinyThumbnail() extends ImageSize
// Image related asset classes
class ImageSet(val large:ImageAsset, val medium:ImageAsset, val small:ImageAsset, val tiny:ImageAsset)
sealed case class Images(protected val contentInfo:ContentInfo, val title:String, val images:ImageSet) extends Asset
// Copy related asset classes
sealed case class Copy(protected val contentInfo:ContentInfo, val title:String, val body:String) extends Asset
// Download related asset classes (video, audio, archive, document)
sealed case class Downloadable(protected val contentInfo:ContentInfo, val title:String, val url:String, val path:String) extends Asset with FileAsset
