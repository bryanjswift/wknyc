package wknyc.business

import org.specs.Specification
import wknyc.Config
import wknyc.model.{WkCredentials,User}

object UserManagerSpecs extends Specification with Sessioned {
	"UserManager.register" should {
		"return Success(user) when saving a valid user" >> {
			val user = WkCredentials("bs@wk.com","password","T","SE",None)
			val saved = UserManager.register(user,Config.Admin)
			saved must beSuccess[WkCredentials]
			saved.payload.uuid must beSome[String]
		}
	}
	"UserManager.authenticate" should {
		"return Some(user) when authenticating a valid user" >> {
			sessioned {
				val user = WkCredentials("bs@wk.com","password","T","SE",None)
				UserManager.register(user,Config.Admin)
				val auth = UserManager.authenticate("bs@wk.com","password")
				auth must beSome[User]
			}
		}
	}
}
