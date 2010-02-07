package wknyc.business

import org.specs.Specification
import wknyc.Config
import wknyc.model.{Employee,WkCredentials,User}

object UserManagerSpecs extends Specification with Sessioned {
	private def save(user:Employee) = UserManager.save(UserManager.encryptPassword(user),Config.Admin)
	private def save(user:WkCredentials) =
		UserManager.save(Employee(UserManager.encryptPassword(user)),Config.Admin)
	"UserManager.register" should {
		"return Success(user) when saving a valid user" >> {
			val user = WkCredentials("bs@wk.com","password","0","SE",None)
			val saved = save(user)
			saved must beSuccess[WkCredentials]
			saved.payload.uuid must beSome[String]
		}
	}
	"UserManager.authenticate" should {
		"return Some(user) when authenticating a valid user" >> {
			sessioned {
				val user = WkCredentials("bs@wk.com","password","3","SE",None)
				save(user)
				val auth = UserManager.authenticate("bs@wk.com","password")
				auth must beSome[User]
			}
		}
		"return None when authenticating an invalid user" >> {
			sessioned {
				val user = WkCredentials("bs@wk.com","password","2","SE",None)
				save(user)
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
			sessioned {
				UserManager.list.toList.size must_== 0
				val user = save(WkCredentials("bs@wk.com","password","2","SE",None)).payload
				UserManager.list.toList.size must_== 1
				UserManager.list.exists(_.username == user.username)
				val user2 = save(WkCredentials("bs2@wk.com","password","3","SE",None)).payload
				UserManager.list.toList.size must_== 2
				UserManager.list.exists(_.username == user.username)
				UserManager.list.exists(_.username == user2.username)
			}
		}
	}
}
