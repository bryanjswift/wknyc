package wknyc.persistence

import javax.jcr.SimpleCredentials
import wknyc.model.Employee

object UserDao {
	def save(employee:Employee) = {
		val session = Config.Repository.login(new SimpleCredentials("admin","".toCharArray),"security")
		try {
			val root = session.getRootNode
			val exists = root.hasNode(employee.username)
			val eNode = if (exists) { root.getNode(employee.username) } else { root.addNode(employee.username) }
			eNode.addMixin("mix:referenceable")
			eNode.addMixin("mix:versionable")
			eNode.setProperty("username",employee.username)
			eNode.setProperty("password",employee.password)
			eNode.setProperty("department",employee.department)
			eNode.setProperty("title",employee.title)
			eNode.setProperty("firstName",employee.firstName)
			eNode.setProperty("lastName",employee.lastName)
			session.save
			Some(eNode.getUUID)
		} catch {
			case e:Exception =>
				None
		} finally {
			session.logout
		}
	}
}
