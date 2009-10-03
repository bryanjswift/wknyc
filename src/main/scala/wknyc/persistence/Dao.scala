package wknyc.persistence

import javax.jcr.SimpleCredentials
import wknyc.model.Employee

object UserDao {
	def save(employee:Employee) = {
		val session = Config.Repository.login(new SimpleCredentials("admin","".toCharArray),"security")
		try {
			val root = session.getRootNode
			val exists = root.hasNode(employee.username)
			val n = if (exists) { root.getNode(employee.username) } else { root.addNode(employee.username) }
			if (exists) { n.checkout }
			n.addMixin("mix:referenceable")
			n.addMixin("mix:versionable")
			n.setProperty("username",employee.username)
			n.setProperty("password",employee.password)
			n.setProperty("department",employee.department)
			n.setProperty("title",employee.title)
			n.setProperty("firstName",employee.firstName)
			n.setProperty("lastName",employee.lastName)
			session.save
			n.checkin
			Some(n.getUUID)
		} catch {
			case e:Exception =>
				None
		} finally {
			session.logout
		}
	}
}
