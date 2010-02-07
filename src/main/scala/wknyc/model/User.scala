package wknyc.model

import java.util.Date
import javax.jcr.Credentials

/** Contain access to all the fields necessary to be a user of the system */
trait User extends Credentials {
	def credentials:WkCredentials
	val uuid:Option[String]
	val username:String = credentials.username
	val password:String = credentials.password
	val role:UserRole = credentials.role
	val title:String = credentials.title
	val active:Boolean = credentials.active
}
object User {
	val NodeType = "wk:credentials"
	val Username = "username"
	val Password = "password"
	val Role = "role"
	val Title = "title"
	val Active = "active"
}
/** Represents interface to personal information for a user */
trait Person {
	def personalInfo:PersonalInfo
	lazy val firstName = personalInfo.firstName
	lazy val lastName = personalInfo.lastName
	lazy val socialNetworks = personalInfo.socialNetworks
}
case class PersonalInfo(firstName:String, lastName:String, socialNetworks:List[SocialNetwork])
object PersonalInfo {
	case object Empty extends PersonalInfo("","",Nil)
}
case class WkCredentials(
	override val username:String,
	override val password:String,
	override val role:UserRole,
	override val title:String,
	override val active:Boolean,
	uuid:Option[String]
) extends User {
	def credentials = this
	// TODO: In Scala 2.8.0 Delete this method
	def cp(uuid:Option[String]):WkCredentials = WkCredentials(username,password,role,title,active,uuid)
	def cp(pass:String):WkCredentials = WkCredentials(username,pass,role,title,active,uuid)
}
object WkCredentials {
	def apply(username:String,password:String,role:String,title:String,uuid:Option[String]):WkCredentials =
		WkCredentials(username,password,UserRole(role),title,true,uuid)
	def apply(username:String,password:String,role:UserRole,title:String,uuid:Option[String]):WkCredentials =
		WkCredentials(username,password,role,title,true,uuid)
}
/** Represent an Employee as a User */
class Employee(
	val contentInfo:ContentInfo, val credentials:WkCredentials, val personalInfo:PersonalInfo
) extends User with Person with Content {
	private def canEqual(a:Any) = a.isInstanceOf[Employee]
	// TODO: In Scala 2.8.0 Delete this method
	def cp(uuid:Option[String]):Employee = Employee(contentInfo.cp(uuid),credentials.cp(uuid),personalInfo)
	def cp(pass:String):Employee = Employee(contentInfo,credentials.cp(pass),personalInfo)
	def equals(e:Employee) =
		contentInfo == e.contentInfo && credentials == e.credentials && personalInfo == e.personalInfo
	override def equals(q:Any) =
		q match {
			case that:Employee =>
				canEqual(q) && equals(that)
			case _ => false
		}
	override def hashCode =
		41 * (41 * (41 + contentInfo.hashCode) + credentials.hashCode) + personalInfo.hashCode
}
object Employee {
	def apply(ci:ContentInfo,credentials:WkCredentials,pi:PersonalInfo) = new Employee(ci,credentials,pi)
	def apply(credentials:WkCredentials) = new Employee(ContentInfo.Empty,credentials,PersonalInfo.Empty)
	val NodeType = "wk:employee"
	val FirstName = "firstName"
	val LastName = "lastName"
}
