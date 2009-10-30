package wknyc.business

import javax.jcr.Session
import wknyc.Config
import wknyc.model.{Employee,User,WkCredentials}
import wknyc.persistence.{Dao,UserDao}

object UserManager {
	def save[T <: User](user:T,loggedIn:User):Option[T] = {
		val session = Config.Repository.login(loggedIn,Config.CredentialsWorkspace)
		using(session,new UserDao(session,Config.Admin))((dao) => {
			try {
				Some(dao.save(user).asInstanceOf[T])
			} catch {
				case e:Exception => None
			}
		})
	}
	def authenticate(username:String,password:String):Option[User] = {
		val session = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		using(session,new UserDao(session,Config.Admin))((dao) => {
			val user = dao.get(username)
			if (user.password == password) Some(user) else None
		})
	}
	def using[T,D <: Dao](session:Session,dao:D)(f:(D) => T) =
		try {
			f(dao)
		} finally {
			dao.close
			session.logout
		}
}
