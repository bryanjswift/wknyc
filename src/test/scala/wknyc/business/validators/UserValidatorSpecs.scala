package wknyc.business.validators

import org.specs.Specification
import wknyc.SHA
import wknyc.model.{SocialNetwork,User,WkCredentials}

object UserValidatorSpecs extends Specification {
	"UserValidator.validate for T where [T <: User]" should {
		"throw IllegalArgumentException if not validating User" >> {
			UserValidator.validate(SocialNetwork("Twitter","http://twitter.com/bjs")) must throwA[IllegalArgumentException]
		}
	}
	"UserValidator.validate for T where [T <: WkCredentials]" should {
		val validCreds = WkCredentials("bs@wk.com","bs","T","SE",None)
		"have a list of only ValidationSuccess instances for valid user" >> {
			val results = UserValidator.validate(validCreds)
			results.size must_== results.filter(result => result.isInstanceOf[ValidationSuccess]).size
		}
		"have a ValidationSuccess instance for each valid value" >> {
			val results = UserValidator.validate(validCreds)
			results mustContain(ValidationSuccess(User.Username))
			results mustContain(ValidationSuccess(User.Password))
			results mustContain(ValidationSuccess(User.Role))
			results mustContain(ValidationSuccess(User.Title))
		}
		"have a ValidationError instance for invalid username" >> {
			val creds = WkCredentials("","bs","T","SE",None)
			val results = UserValidator.validate(creds)
			results mustContain(ValidationError(User.Username,String.format("%s can not be empty.",User.Username)))
		}
		"have a ValidationError instance for empty password" >> {
			val creds = WkCredentials("bs","","T","SE",None)
			val results = UserValidator.validate(creds)
			results mustContain(ValidationError(User.Password,String.format("%s can not be empty.",User.Password)))
		}
		"have a ValidationError instance for empty encrypted password" >> {
			val creds = WkCredentials("bs",SHA(""),"T","SE",None)
			val results = UserValidator.validate(creds)
			results mustContain(ValidationError(User.Password,String.format("%s can not be empty.",User.Password)))
		}
	}
	// TODO: Add validation specs for Employee
}
