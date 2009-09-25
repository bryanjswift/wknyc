package wknyc.model

import java.util.Date

/** Contain access to all the fields necessary to be a user of the system */
trait User extends Content {
	val email:String
	val password:String
	val firstName:String
	val lastName:String
	val socialNetworks:List[SocialNetwork]
}
/** Represent an Employee as a User */
class Employee(
	protected val contentInfo:ContentInfo,
	val email:String, val password:String, val firstName:String,
	val lastName:String, val socialNetworks:List[SocialNetwork],
	val department:String, val title:String
) extends User
