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
						Success(dao.save(user))
					} catch {
						case e:Exception => Failure(List(Error(e)),"Unable to create account for " + user.username)
					}
				)
			case _ =>
				Failure(errors)
		}
	}
	/** Retrieve an existing User
		* @param uuid of User to retrieve
		* @param loggedIn - User who is creating the new user
		* @returns User with the given uuid if one exists
		*/
	def get(uuid:String) = using(new UserDao(Config.Admin)) { _.get(uuid) }
	/** If the user id is empty then encrypt the password and return a copy of
		* the user
		* @param user whose password needs encrypting
		* @returns User with an encrypted password
		*/
	def encryptPassword[T <: User](user:T):T =
		(user match {
			case creds:WkCredentials => creds.cp(SHA(creds.password))
			case emp:Employee => emp.cp(SHA(emp.password))
			case _ => user
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
