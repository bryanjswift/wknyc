package wknyc.business.validators

import wknyc.model.{Employee,PersonalInfo,User,UserRole,WkCredentials}
import wknyc.model.Employee._
import wknyc.model.User._

object UserValidator extends Validator {
	// do the work
	override def validate(user:AnyRef):List[ValidationResult] =
		user match {
			case creds:WkCredentials => validateCredentials(creds)
			case employee:Employee => validateEmployee(employee)
			case _ => throw new IllegalArgumentException(String.format("%s is not a known User type",user.getClass.getName))
		}
	// Type Validation
	private def validateCredentials(creds:WkCredentials) = ( // this is one statement
		validateUsername(creds.username)
		:: validatePassword(creds.password)
		:: validateRole(creds.role)
		:: validateTitle(creds.title)
		:: Nil
	)
	private def validateEmployee(employee:Employee) =
		validateCredentials(employee.credentials) ::: validatePersonalInfo(employee.personalInfo)
	private def validatePersonalInfo(info:PersonalInfo) = ( // this is one statement
		validateFirstName(info.firstName)
		:: validateLastName(info.lastName)
		:: Nil
	)
	// Credentials Field Validation
	private def validateUsername(username:String) = required(username,Username)
	private def validatePassword(password:String) = required(password,Password)
	private def validateRole(role:UserRole) = notValidated(Role)
	private def validateTitle(title:String) = notValidated(Title)
	// PersonalInfo Field Validation
	private def validateFirstName(firstName:String) = notValidated(FirstName)
	private def validateLastName(lastName:String) = notValidated(LastName)
}
