package wknyc.model

import java.util.Calendar
import wknyc.Config

/** Class to hold universal content information */
class ContentInfo(val created:Calendar, val modified:Calendar, val modifierId:Option[String], val uuid:Option[String]) {
	def this(created:Calendar,modified:Calendar,modifier:User,uuid:Option[String]) =
		this(created,modified,modifier.uuid,uuid)
	def cp(uuid:Option[String]) = ContentInfo(created, modified, modifierId, uuid)
	def modifiedBy(user:User) = ContentInfo(created, Calendar.getInstance, user, uuid)
	/** ContentInfo refers to the same Content if created and uuid are the same */
	def equals(ci:ContentInfo) = created == ci.created && uuid == ci.uuid
	override def equals(obj:Any) =
		obj match {
			case that:ContentInfo => equals(that) && hashCode == that.hashCode
			case _ => false
		}
	override val hashCode =
		41 * (41 + created.hashCode) + uuid.hashCode
	override val toString =
		List(created.getTimeInMillis,uuid.toString).mkString("ContentInfo(",", ",")")
}
// Companion object for ContentInfo, nothing interesting to say
object ContentInfo {
	def apply(user:User) = create(user)
	def apply(created:Calendar, modified:Calendar, modifierId:Option[String], uuid:Option[String]) =
		new ContentInfo(created, modified, modifierId, uuid)
	def apply(created:Calendar, modified:Calendar, modifiedBy:User, uuid:Option[String]) =
		new ContentInfo(created, modified, modifiedBy, uuid)
	def create(user:User) = {
		val now = Calendar.getInstance
		new ContentInfo(now, now, user, None)
	}
	// If Empty ever gets saved things will break
	object Empty extends ContentInfo(Calendar.getInstance,null,Config.Admin,None)
}
/** Trait to be mixed into objects to allow direct access to data stored in ContentInfo
	* without having to extend ContentInfo */
trait Content {
	def contentInfo:ContentInfo
	lazy val created:Calendar = contentInfo.created
	lazy val modified:Calendar = contentInfo.modified
	lazy val modifierId:Option[String] = contentInfo.modifierId
	lazy val uuid:Option[String] = contentInfo.uuid
}
object Content {
	val NodeType = "wk:content"
	val Created = "created"
	val Modified = "modified"
	val ModifiedBy = "modifiedBy"
}
/** Trait to be mixed into objects which must maintain a specific orderering */
trait Ordered {
	def position:Long
}
object Ordered {
	val NodeType = "wk:ordered"
	val Position = "position"
}
