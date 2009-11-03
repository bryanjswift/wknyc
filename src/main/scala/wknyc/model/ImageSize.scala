package wknyc.model

/** Size of image trait and related objects */
sealed trait ImageSize {
	def name:String
	def width:Int
	def height:Int
}
object ImageSize {
	def apply(name:String) =
		name match {
			case FullThumbnail.name => FullThumbnail
			case LargeThumbnail.name => LargeThumbnail
			case SidebarThumbnail.name => SidebarThumbnail
			case MediumThumbnail.name => MediumThumbnail
			case SmallThumbnail.name => SmallThumbnail
			case TinyThumbnail.name => TinyThumbnail
		}
}
// TODO: need dimensions for FullThumbnail
case object FullThumbnail extends ImageSize {
	val name = "full"
	val width = 634
	val height = 535
}
case object LargeThumbnail extends ImageSize {
	val name = "large"
	val width = 634
	val height = 535
}
case object MediumThumbnail extends ImageSize {
	val name = "medium"
	val width = 272
	val height = 227
}
case object SidebarThumbnail extends ImageSize {
	val name = "sidebar"
	val width = 133
	val height = 89
}
case object SmallThumbnail extends ImageSize {
	val name = "small"
	val width = 104
	val height = 92
}
case object TinyThumbnail extends ImageSize {
	val name = "tiny"
	val width = 26
	val height = 26
}