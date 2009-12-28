package wknyc.business

import WkPredef._
import wknyc.business.validators.{Error,UserValidator,ValidationResult}
import wknyc.model.{Employee,User,WkCredentials}
import wknyc.persistence.UserDao
import wknyc.{Config,SHA,WkPredef}

object UserManager extends Manager {
	/** Perform user saving to repository
		* @param user to be saved
		* @param loggedIn - User who is creating the new user
		* @returns Success[User] if user was successfully persisted Failure otherwise
		*/
	def save[T <: User](user:T,loggedIn:User):Response[T] = {
		val errors = UserValidator.errors(user)
		errors match {
			case Nil =>
				using(new UserDao(loggedIn))(dao =>
					try {
						Success(dao.save(encryptPassword(user)))
					} catch {
						case e:Exception => Failure(List(Error(e)),"Unable to create account for " + user.username)
					}
				)
			case _ =>
				Failure(errors)
		}
	}
	private def encryptPassword[T <: User](user:T):T =
		(if (user.uuid.isEmpty) {
			user match {
				case creds:WkCredentials => creds.cp(SHA(creds.password))
				case emp:Employee => emp.cp(SHA(emp.password))
				case _ => user
			}
		} else {
			user
		}).asInstanceOf[T]
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
			if (user.password == SHA(password) && user.active) Some(user) else None
		})
}
