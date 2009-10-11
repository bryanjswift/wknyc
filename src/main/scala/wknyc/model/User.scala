package wknyc.model

import java.util.Date
import javax.jcr.Credentials

/** Contain access to all the fields necessary to be a user of the system */
trait User extends Credentials {
	def credentials:WkCredentials
	val uuid:Option[String]
	val username:String = credentials.username
	val password:String = credentials.password
	val department:String = credentials.department
	val title:String = credentials.title
}
object User {
	val NodeType = "wk:credentials"
	val Username = "username"
	val Password = "password"
	val Department = "department"
	val Title = "title"
}
/** Represents interface to personal information for a user */
trait Person {
	def personalInfo:PersonalInfo
	lazy val firstName = personalInfo.firstName
	lazy val lastName = personalInfo.lastName
	lazy val socialNetworks = personalInfo.socialNetworks
}
case class PersonalInfo(firstName:String, lastName:String, socialNetworks:List[SocialNetwork])
case class WkCredentials(
	override val username:String,
	override val password:String,
	override val department:String,
	override val title:String,
	uuid:Option[String]
) extends User {
	def credentials = this
	// TODO: In Scala 2.8.0 Delete this method
	def cp(uuid:String) = WkCredentials(username,password,department,title,Some(uuid))
}
/** Represent an Employee as a User */
class Employee(
	val contentInfo:ContentInfo, val credentials:WkCredentials, val personalInfo:PersonalInfo
) extends User with Person with Content {
	private def canEqual(a:Any) = a.isInstanceOf[Employee]
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
	val NodeType = "wk:employee"
	val FirstName = "firstName"
	val LastName = "lastName"
}
