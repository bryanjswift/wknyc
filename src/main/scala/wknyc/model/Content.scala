package wknyc.model

import org.joda.time.DateTime

/** Class to hold universal content information */
class ContentInfo(val dateCreated:DateTime, val lastModified:DateTime, val modifiedBy:User, val uuid:Option[String]) {
	def modify(user:User) = new ContentInfo(dateCreated, new DateTime, user, uuid)
	def cp(uuid:String) = new ContentInfo(dateCreated, lastModified, modifiedBy, Some(uuid))
	private def canEqual(a:Any) = a.isInstanceOf[ContentInfo]
	def equals(ci:ContentInfo) = 
		dateCreated == ci.dateCreated && lastModified == ci.lastModified && modifiedBy == ci.modifiedBy && uuid == ci.uuid
	override def equals(q:Any) =
		q match {
			case that:ContentInfo =>
				canEqual(q) && equals(that)
			case _ => false
		}
	override def hashCode =
		41 * (41 * (41 * (41 + dateCreated.hashCode) + lastModified.hashCode) + modifiedBy.hashCode) + uuid.hashCode
}
// Companion object for ContentInfo, nothing interesting to say
object ContentInfo {
	def apply(user:User) = create(user)
	def create(user:User) = {
		val now = new DateTime
		new ContentInfo(now, now, user, None)
	}
}
/** Trait to be mixed into objects to allow direct access to data stored in ContentInfo
	* without having to extend ContentInfo */
trait Content {
	def contentInfo:ContentInfo
	lazy val dateCreated:DateTime = contentInfo.dateCreated
	lazy val lastModified:DateTime = contentInfo.lastModified
	lazy val modifiedBy:User = contentInfo.modifiedBy
	lazy val uuid:Option[String] = contentInfo.uuid
}
object Content {
	val NodeType = "wk:content"
	val DateCreated = "dateCreated"
	val LastModified = "lastModified"
	val ModifiedBy = "modifiedBy"
}
/** Trait to be mixed into objects which must maintain a specific orderering */
trait OrderedContent[T <: OrderedContent[T]] extends Content {
	def position:Int
	def siblings:List[T]
}
