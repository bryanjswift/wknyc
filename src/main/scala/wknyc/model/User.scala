package wknyc.model

import java.util.Date
import javax.jcr.Credentials

/** Contain access to all the fields necessary to be a user of the system */
trait User extends Credentials {
	def credentials:WkCredentials
	val password:String = credentials.password
	val username:String = credentials.email
}
/** Represents interface to personal information for a user */
trait Person {
	def personalInfo:PersonalInfo
	lazy val firstName = personalInfo.firstName
	lazy val lastName = personalInfo.lastName
	lazy val socialNetworks = personalInfo.socialNetworks
}
case class PersonalInfo(val firstName:String, val lastName:String, val socialNetworks:List[SocialNetwork])
case class WkCredentials(val email:String, override val password:String, val department:String, val title:String) extends User {
	def credentials = this
}
/** Represent an Employee as a User */
class Employee(
	val contentInfo:ContentInfo, val credentials:WkCredentials, val personalInfo:PersonalInfo
) extends User with Person with Content {
	lazy val department = credentials.department
	lazy val title = credentials.title
}
object Employee {
	def apply(ci:ContentInfo,credentials:WkCredentials,pi:PersonalInfo) = new Employee(ci,credentials,pi)
}
