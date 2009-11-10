package wknyc.business.validators

import wknyc.model.{Employee,User,WkCredentials}

object UserValidator {
	def validateCredentials(username:Option[String],password:Option[String],department:Option[String],title:Option[String]) =
		(
			validateUsername(username)
			:: validatePassword(password)
			:: validateDepartment(department)
			:: validateTitle(title)
			:: Nil
		)
	private def validateUsername(username:Option[String]):ValidationResult =
		username match {
			case None => ValidationError("username","Username is required")
			case _ => ValidationSuccess("username")
		}
	private def validatePassword(username:Option[String]):ValidationResult =
		username match {
			case None => ValidationError("password","Password is required")
			case _ => ValidationSuccess("password")
		}
	private def validateDepartment(department:Option[String]):ValidationResult =
		department match {
			case _ => ValidationSuccess("department")
		}
	private def validateTitle(title:Option[String]):ValidationResult =
		title match {
			case _ => ValidationSuccess("title")
		}
}
