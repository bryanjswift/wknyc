package wknyc.business

import javax.jcr.Session
import wknyc.model.User
import wknyc.persistence.Dao

trait Manager {
	type D <: Dao
	def using[T](session:Session,dao:D)(f:(D) => T):T =
		try {
			f(dao)
		} finally {
			dao.close
			session.logout
		}
}
