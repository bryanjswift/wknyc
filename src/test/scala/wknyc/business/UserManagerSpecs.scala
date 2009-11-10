package wknyc.business

import org.specs.Specification
import wknyc.Config
import wknyc.model.{WkCredentials,User}

object UserManagerSpecs extends Specification {
	"UserManager" should {
		"return Some(user) when saving a valid user" >> {
			val user = WkCredentials("bs@wk.com","password","T","SE",None)
			val saved = UserManager.register(user,Config.Admin)
			saved must beSome[WkCredentials]
			saved.get.uuid must beSome[String]
		}
		"return Some(user) when authenticating a valid user" >> {
			// open session so repository doesn't shut down during test
			val session = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
			val user = WkCredentials("bs@wk.com","password","T","SE",None)
			UserManager.register(user,Config.Admin)
			val auth = UserManager.authenticate("bs@wk.com","password")
			auth must beSome[User]
			// logout so repository can shut down
			session.logout
		}
	}
}
