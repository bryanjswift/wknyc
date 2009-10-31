package wknyc.business.validators

import wknyc.model.{Employee,User,WkCredentials}

object UserValidator {
	def validateCredentials(username:Option[String],password:Option[String],department:Option[String],title:Option[String]) = {
		validateUsername(username) ::: validatePassword(password) ::: validateDepartment(department) ::: validateTitle(title) ::: Nil
	}
	private def validateUsername(username:Option[String]):List[ValidationError] =
		username match {
			case None => List(ValidationError("username","Username is required"))
			case _ => Nil
		}
	private def validatePassword(username:Option[String]):List[ValidationError] =
		username match {
			case None => List(ValidationError("password","Password is required"))
			case _ => Nil
		}
	private def validateDepartment(department:Option[String]):List[ValidationError] =
		department match {
			case _ => Nil
		}
	private def validateTitle(title:Option[String]):List[ValidationError] =
		title match {
			case _ => Nil
		}
}
