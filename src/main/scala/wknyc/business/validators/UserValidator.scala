package wknyc.business.validators

import wknyc.model.{Employee,WkCredentials}
import wknyc.model.User._

object UserValidator {
	private val emptyRE = "^$".r
	def validateCredentials(creds:WkCredentials) = ( // using parens because this is one statement
		validateUsername(creds.username)
		:: validatePassword(creds.password)
		:: validateDepartment(creds.department)
		:: validateTitle(creds.title)
		:: Nil
	)
	private def validateUsername(username:String):ValidationResult =
		username match {
			case emptyRE(empty) => ValidationError(Username,"Username is required")
			case _ => ValidationSuccess(Username)
		}
	private def validatePassword(username:String):ValidationResult =
		username match {
			case emptyRE(empty) => ValidationError(Password,"Password is required")
			case _ => ValidationSuccess(Password)
		}
	private def validateDepartment(department:String):ValidationResult =
		department match {
			case _ => ValidationSuccess(Department)
		}
	private def validateTitle(title:String):ValidationResult =
		title match {
			case _ => ValidationSuccess(Title)
		}
}
