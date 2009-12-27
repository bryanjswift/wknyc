package wknyc.business

import WkPredef._
import wknyc.business.validators.{Error,UserValidator,ValidationResult}
import wknyc.model.User
import wknyc.persistence.UserDao
import wknyc.{Config, WkPredef}

object UserManager extends Manager {
	def register[T <: User](user:T,loggedIn:User) = save(user,loggedIn)
	private def save[T <: User](user:T,loggedIn:User):Response[_ <: T] = {
		val errors = UserValidator.errors(user)
		errors match {
			case Nil =>
				using(new UserDao(loggedIn))(dao =>
					try {
						Success(dao.save(user))
					} catch {
						case e:Exception => Failure(List(Error(e)),"Unable to create account for " + user.username)
					}
				)
			case _ =>
				Failure(errors)
		}
	}
	def list = using(new UserDao(Config.Admin)) { _.list }
	def authenticate(username:String,password:String):Option[User] =
		using(new UserDao(Config.Admin))((dao) => {
			val user = dao.get(username)
			if (user.password == password && user.active) Some(user) else None
		})
}
