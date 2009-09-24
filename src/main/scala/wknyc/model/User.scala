package wknyc.model

import java.util.Date

class User(
	val dateCreated:Date, val lastModified:Date, val modifiedBy:User,
	val email:String, val password:String, val firstName:String,
	val lastName:String, val socialNetworks:List[SocialNetwork]
) extends Content

class Employee(
	dateCreated:Date, lastModified:Date, modifiedBy:User, email:String, password:String, firstName:String,
	lastName:String, socialNetworks:List[SocialNetwork],
	val department:String, val title:String
) extends User(dateCreated, lastModified, modifiedBy, email, password, firstName, lastName, socialNetworks)
