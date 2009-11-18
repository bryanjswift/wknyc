package wknyc.business

import WkPredef._
import wknyc.business.validators.{Error,UserValidator,ValidationSuccess}
import wknyc.model.User
import wknyc.persistence.UserDao
import wknyc.{Config, WkPredef}

object UserManager extends Manager {
	type D = UserDao
	def register[T <: User](user:T,loggedIn:User) = save(user,loggedIn)
	private def save[T <: User](user:T,loggedIn:User):Response[_ <: T] = {
		val session = Config.Repository.login(loggedIn,Config.CredentialsWorkspace)
		val errors = UserValidator.errors(user)
		errors match {
			case Nil =>
				using(session,new UserDao(loggedIn))((dao) =>
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
	def authenticate(username:String,password:String):Option[User] = {
		val session = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		using(session,new UserDao(Config.Admin))((dao) => {
			val user = dao.get(username)
			if (user.password == password) Some(user) else None
		})
	}
}
