package wknyc

import javax.jcr.Session
import wknyc.persistence.Dao

object WkPredef {
	def using[T,D <: Dao](session:Session,dao:D)(f:(D) => T) =
		try {
			f(dao)
		} finally {
			dao.close
			session.logout
		}
	val Success = business.Success
	val Failure = business.Failure
}
