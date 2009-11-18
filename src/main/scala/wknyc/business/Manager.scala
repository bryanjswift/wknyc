package wknyc.business

import javax.jcr.Session
import wknyc.model.User
import wknyc.persistence.Dao

trait Manager {
	def using[T,D <: Dao](dao:D)(f:(D) => T) =
		try {
			f(dao)
		} finally {
			dao.close
		}
}
