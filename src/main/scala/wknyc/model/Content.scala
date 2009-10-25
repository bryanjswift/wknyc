package wknyc.model

import java.util.Calendar

/** Class to hold universal content information */
class ContentInfo(val dateCreated:Calendar, val lastModified:Calendar, val modifiedBy:User, val uuid:Option[String]) {
	def cp(uuid:String) = ContentInfo(dateCreated, lastModified, modifiedBy, Some(uuid))
	def modify(user:User) = ContentInfo(dateCreated, Calendar.getInstance, user, uuid)
	/** ContentInfo refers to the same Content if dateCreated and uuid are the same */
	def equals(ci:ContentInfo) = dateCreated == ci.dateCreated && uuid == ci.uuid
	override def equals(obj:Any) =
		obj match {
			case that:ContentInfo => equals(that) && hashCode == that.hashCode
			case _ => false
		}
	override val hashCode =
		41 * (41 + dateCreated.hashCode) + uuid.hashCode
	override val toString =
		List(dateCreated.getTimeInMillis,lastModified.getTimeInMillis,modifiedBy.username,uuid.toString).mkString("ContentInfo(",", ",")")
}
// Companion object for ContentInfo, nothing interesting to say
object ContentInfo {
	def apply(user:User) = create(user)
	def apply(dateCreated:Calendar, lastModified:Calendar, modifiedBy:User, uuid:Option[String]) =
		new ContentInfo(dateCreated, lastModified, modifiedBy, uuid)
	def create(user:User) = {
		val now = Calendar.getInstance
		new ContentInfo(now, now, user, None)
	}
}
/** Trait to be mixed into objects to allow direct access to data stored in ContentInfo
	* without having to extend ContentInfo */
trait Content {
	def contentInfo:ContentInfo
	lazy val dateCreated:Calendar = contentInfo.dateCreated
	lazy val lastModified:Calendar = contentInfo.lastModified
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
