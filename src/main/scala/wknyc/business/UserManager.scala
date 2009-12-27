package wknyc.business

import WkPredef._
import wknyc.business.validators.{Error,UserValidator,ValidationResult}
import wknyc.model.User
import wknyc.persistence.UserDao
import wknyc.{Config, WkPredef}

object UserManager extends Manager {
	/** Store new User in repository
		* @param user to be saved
		* @param loggedIn - User who is creating the new user
		* @returns Success[User] if user was successfully persisted Failure otherwise
		*/
	def register[T <: User](user:T,loggedIn:User) = save(user,loggedIn)
	/** Perform user saving to repository
		* @param user to be saved
		* @param loggedIn - User who is creating the new user
		* @returns Success[User] if user was successfully persisted Failure otherwise
		*/
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
	/** Provide Iterable[User] containing all saved Users
		* @returns Iterable[User]
		*/
	def list = using(new UserDao(Config.Admin)) { _.list }
	/** Check username password against the set of stored credentials
		* @param username of User to check
		* @param password to validate for username
		* @returns Some[User] if validation succeeded None otherwise
		*/
	def authenticate(username:String,password:String):Option[User] =
		using(new UserDao(Config.Admin))((dao) => {
			val user = dao.get(username)
			if (user.password == password && user.active) Some(user) else None
		})
}
