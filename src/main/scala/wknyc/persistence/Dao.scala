package wknyc.persistence

import javax.jcr.Session
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,User,WkCredentials}

/** UserDao is created to save and retrieve User type objects from the repository
	* @param session is used to access the repository but is not modified (logged out)
	* @param loggedInUser is used to set lastModifiedUser of content being saved
	*/
class UserDao(private val session:Session, private val loggedInUser:User) {
	// should return an employee object with uuid's added (if new)
	// date created and date modified values should also be updated
	def save(employee:Employee) = {
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
		}
	}
	private def saveUser(user:User) = {
	}
	def get(uuid:String) = {
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
		}
	}
}
