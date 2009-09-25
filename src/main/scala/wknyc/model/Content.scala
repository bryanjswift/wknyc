package wknyc.model

import java.util.Date

/** Class to hold universal content information */
class ContentInfo(val dateCreated:Date, val lastModified:Date, val modifiedBy:User) {
	def modify(user:User) = new ContentInfo(dateCreated, new Date, user)
}
// Companion object for ContentInfo, nothing interesting to say
object ContentInfo {
	def apply(user:User) = {
		val now = new Date
		new ContentInfo(now, now, user)
	}
}
/** Trait to be mixed into objects to allow direct access to data stored in ContentInfo
	* without having to extend ContentInfo */
trait Content {
	protected def contentInfo:ContentInfo
	lazy val dateCreated:Date = contentInfo.dateCreated
	lazy val lastModified:Date = contentInfo.lastModified
	lazy val modifiedBy:User = contentInfo.modifiedBy
}
/** Trait to be mixed into objects which must maintain a specific orderering */
trait OrderedContent[T <: OrderedContent[T]] {
	val position:Int
	val siblings:List[T]
}
