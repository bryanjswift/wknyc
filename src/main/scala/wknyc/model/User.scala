package wknyc.model

import java.util.Date
import javax.jcr.Credentials

/** Contain access to all the fields necessary to be a user of the system */
trait User extends Credentials {
	def credentials:WkCredentials
	val uuid:Option[String]
	val password:String = credentials.passw
	val username:String = credentials.email
}
/** Represents interface to personal information for a user */
trait Person {
	def personalInfo:PersonalInfo
	lazy val firstName = personalInfo.firstName
	lazy val lastName = personalInfo.lastName
	lazy val socialNetworks = personalInfo.socialNetworks
}
case class PersonalInfo(firstName:String, lastName:String, socialNetworks:List[SocialNetwork])
case class WkCredentials(email:String, passw:String, department:String, title:String) extends User {
	def credentials = this
	val uuid = None
}
/** Represent an Employee as a User */
class Employee(
	val contentInfo:ContentInfo, val credentials:WkCredentials, val personalInfo:PersonalInfo, val id:String
) extends User with Person with Content {
	lazy val uuid = if (id.length > 0) Some(id) else None
	lazy val department = credentials.department
	lazy val title = credentials.title
}
object Employee {
	def apply(ci:ContentInfo,credentials:WkCredentials,pi:PersonalInfo) = new Employee(ci,credentials,pi,"")
	def apply(ci:ContentInfo,credentials:WkCredentials,pi:PersonalInfo,id:String) = new Employee(ci,credentials,pi,id)
}
