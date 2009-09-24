package wknyc.model

import java.util.Date

trait User extends Content {
	val dateCreated:Date
	val lastModified:Date
	val modifiedBy:User
	val email:String
	val password:String
	val firstName:String
	val lastName:String
	val socialNetworks:List[SocialNetwork]
}

sealed case class Employee(
	val dateCreated:Date, val lastModified:Date, val modifiedBy:User,
	val email:String, val password:String, val firstName:String,
	val lastName:String, val socialNetworks:List[SocialNetwork],
	val department:String, val title:String
) extends User
