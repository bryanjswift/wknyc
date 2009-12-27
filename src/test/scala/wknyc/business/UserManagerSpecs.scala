package wknyc.business

import org.specs.Specification
import wknyc.Config
import wknyc.model.{WkCredentials,User}

object UserManagerSpecs extends Specification with Sessioned {
	"UserManager.register" should {
		"return Success(user) when saving a valid user" >> {
			val user = WkCredentials("bs@wk.com","password","0","SE",None)
			val saved = UserManager.register(user,Config.Admin)
			saved must beSuccess[WkCredentials]
			saved.payload.uuid must beSome[String]
		}
	}
	"UserManager.authenticate" should {
		"return Some(user) when authenticating a valid user" >> {
			sessioned {
				val user = WkCredentials("bs@wk.com","password","3","SE",None)
				UserManager.register(user,Config.Admin)
				val auth = UserManager.authenticate("bs@wk.com","password")
				auth must beSome[User]
			}
		}
		"return None when authenticating an invalid user" >> {
			sessioned {
				val user = WkCredentials("bs@wk.com","password","2","SE",None)
				UserManager.register(user,Config.Admin)
				val auth = UserManager.authenticate("bs@wk.com","wrong")
				auth must beNone
			}
		}
	}
	"UserManager.list" should {
		"return empty Iterable[User] when no users have been saved" >> {
			UserManager.list.toList.size must_== 0
		}
		"return an Iterable[User] with all saved users" >> {
			val admin = Config.Admin
			sessioned {
				UserManager.list.toList.size must_== 0
				val user = UserManager.register(WkCredentials("bs@wk.com","password","2","SE",None),admin).payload
				UserManager.list.toList.size must_== 1
				UserManager.list.exists(_.username == user.username)
				val user2 = UserManager.register(WkCredentials("bs2@wk.com","password","3","SE",None),admin).payload
				UserManager.list.toList.size must_== 2
				UserManager.list.exists(_.username == user.username)
				UserManager.list.exists(_.username == user2.username)
			}
		}
	}
}
