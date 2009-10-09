package wknyc.model

import java.util.Calendar

/** Class to hold universal content information */
class ContentInfo(val dateCreated:Calendar, val lastModified:Calendar, val modifiedBy:User) {
	def modify(user:User) = new ContentInfo(dateCreated, Calendar.getInstance, user)
	private def canEqual(a:Any) = a.isInstanceOf[ContentInfo]
	def equals(ci:ContentInfo) = 
		this.dateCreated == ci.dateCreated && this.lastModified == ci.lastModified && this.modifiedBy == ci.modifiedBy
	override def equals(q:Any) =
		q match {
			case that:ContentInfo =>
				canEqual(q) && equals(that)
			case _ => false
		}
	override def hashCode =
		41 * (41 * (41 + dateCreated.hashCode) + lastModified.hashCode) + modifiedBy.hashCode
}
// Companion object for ContentInfo, nothing interesting to say
object ContentInfo {
	def apply(user:User) = create(user)
	def create(user:User) = {
		val now = Calendar.getInstance
		new ContentInfo(now, now, user)
	}
}
/** Trait to be mixed into objects to allow direct access to data stored in ContentInfo
	* without having to extend ContentInfo */
trait Content {
	def contentInfo:ContentInfo
	lazy val dateCreated:Calendar = contentInfo.dateCreated
	lazy val lastModified:Calendar = contentInfo.lastModified
	lazy val modifiedBy:User = contentInfo.modifiedBy
}
/** Trait to be mixed into objects which must maintain a specific orderering */
trait OrderedContent[T <: OrderedContent[T]] extends Content {
	def position:Int
	def siblings:List[T]
}
