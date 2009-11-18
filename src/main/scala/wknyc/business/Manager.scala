package wknyc.business

import javax.jcr.Session
import wknyc.model.User
import wknyc.persistence.Dao

trait Manager {
	type D <: Dao
	/* TODO: Figure out how to do this so Dao instances don't have to be created
	 * in each manager whenever using is called. If new instances of D can be instantiated
	 * here it is necessary only to pass the user
	 * /
	val workspace:String
	def using[T](user:User)(f:(D) => T):T = {
		val session = Config.Repository.login(user,workspace)
		using(session,new D(session,user))(f)
	}
	*/
	def using[T](session:Session,dao:D)(f:(D) => T):T =
		try {
			f(dao)
		} finally {
			dao.close
			session.logout
		}
}
