package wknyc.business.validators

import wknyc.model.{Employee,User,WkCredentials}
import wknyc.model.User._

object UserValidator {
	private val emptyRE = "^$".r
	def validate(user:User) =
		user match {
			case creds:WkCredentials => validateCredentials(creds)
			case employee:Employee => validateEmployee(employee)
		}
	private def validateCredentials(creds:WkCredentials) = ( // using parens because this is one statement
		validateUsername(creds.username)
		:: validatePassword(creds.password)
		:: validateDepartment(creds.department)
		:: validateTitle(creds.title)
		:: Nil
	)
	private def validateEmployee(employee:Employee) = ( // using parens because this is one statement
		validateCredentials(employee.credentials)
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
