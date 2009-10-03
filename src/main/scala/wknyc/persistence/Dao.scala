package wknyc.persistence

import javax.jcr.SimpleCredentials
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,WkCredentials}

object UserDao {
	def save(employee:Employee) = {
		val session = Config.Repository.login(new SimpleCredentials("admin","".toCharArray),"security")
		try {
			val root = session.getRootNode
			val exists = root.hasNode(employee.username)
			val n = if (exists) { root.getNode(employee.username) } else { root.addNode(employee.username) }
			if (exists) {
				n.checkout
			} else {
				n.addMixin("mix:referenceable")
				n.addMixin("mix:versionable")
			}
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
	def get(uuid:String) = {
		val session = Config.Repository.login(new SimpleCredentials("admin","".toCharArray),"security")
		try {
			val n = session.getNodeByUUID(uuid)
			Employee(
				ContentInfo(WkCredentials("","","","")),
				WkCredentials(
					n.getProperty("username").getString,
					n.getProperty("password").getString,
					n.getProperty("department").getString,
					n.getProperty("title").getString
				),
				PersonalInfo(
					n.getProperty("firstName").getString,
					n.getProperty("lastName").getString,
					List[SocialNetwork]()
				)
			)
		} finally {
			session.logout
		}
	}
}
