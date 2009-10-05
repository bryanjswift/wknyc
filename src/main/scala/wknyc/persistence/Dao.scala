package wknyc.persistence

import javax.jcr.SimpleCredentials
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,WkCredentials}

// Maybe the Dao's are classes and are created with the User objects for the logged in user
object UserDao {
	// should return an employee object with uuid's added (if new)
	// date created and date modified values should also be updated
	def save(employee:Employee) = {
		val session = Config.Repository.login(WkCredentials("admin","","","",None),"security")
		try {
Console.println(session.getUserID)
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
//		} catch {
//			case e:Exception =>
//				None
		} finally {
			session.logout
		}
	}
	def get(uuid:String) = {
		val session = Config.Repository.login(WkCredentials("","","","",None),"security")
		try {
			val n = session.getNodeByUUID(uuid)
			Employee(
				ContentInfo(WkCredentials("","","","",None)),
				WkCredentials(
					n.getProperty("username").getString,
					n.getProperty("password").getString,
					n.getProperty("department").getString,
					n.getProperty("title").getString,
					None
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
