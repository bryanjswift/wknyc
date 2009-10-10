package wknyc.persistence

import javax.jcr.{Node,NodeIterator,Session}
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,User,WkCredentials}

/** UserDao is created to save and retrieve User type objects from the repository
	* @param session is used to access the repository but is not modified (logged out)
	* @param loggedInUser is used to set lastModifiedUser of content being saved
	*/
class UserDao(session:Session, loggedInUser:User) extends Dao(session,loggedInUser) {
	/** Save an object which is at least of type User
		* @param user to be saved delegates to saveCredentials or saveEmployee depending on type
		* @returns T with uuid populated and lastModified/modifiedBy fields updated (if applicable)
		*/
	def save[T <: User](user:T):T =
		user match {
			case employee:Employee => saveEmployee(employee).asInstanceOf[T]
			case credentials:WkCredentials => saveCredentials(credentials).asInstanceOf[T]
		}
	/** Save a WkCredentials object to the repository
		* @param credentials to be saved
		* @returns WkCredentials with uuid populated
		*/
	private def saveCredentials(credentials:WkCredentials) = {
		val n = getNode(credentials.username,User.NodeType)
		writeProperties(n,credentials)
		session.save
		n.checkin
		WkCredentials(
			credentials.username,
			credentials.password,
			credentials.department,
			credentials.title,
			Some(n.getUUID)
		)
	}
	/** Save an Employee to the repository
		* @param employee to be saved
		* @returns Employee with uuid, lastModified and modifiedBy updated
		*/
	private def saveEmployee(employee:Employee) = {
		val ci = employee.contentInfo.modify(loggedInUser) // do it first so saved data and object have same values
		val n = getNode(employee.username,Employee.NodeType)
		writeProperties(n,employee,ci)
		session.save
		n.checkin
		Employee(
			ci,
			// TODO: In Scala 2.8.0 change this to use copy(uuid = Some(n.getUUID))
			employee.credentials.cp(n.getUUID),
			employee.personalInfo,
			n.getUUID
		)
	}
	/** Write general user properties to provided node
		* @param n - node to write data to
		* @param user whose data should be written
		*/
	private def writeProperties(n:Node,user:User):Unit = {
		n.setProperty("username",user.username)
		n.setProperty("password",user.password)
		n.setProperty("department",user.department)
		n.setProperty("title",user.title)
	}
	/** Write employee information to provided node
		* @param n - node to write data to
		* @param employee object containing data to write
		* @param ci ContentInfo with datestamps and user tracking to write
		*/
	private def writeProperties(n:Node,employee:Employee,ci:ContentInfo):Unit = {
		writeProperties(n,employee)
		n.setProperty("firstName",employee.firstName)
		n.setProperty("lastName",employee.lastName)
		n.setProperty("dateCreated",ci.dateCreated)
		n.setProperty("lastModified",ci.lastModified)
		n.setProperty("modifiedBy", session.getNodeByUUID(loggedInUser.uuid.get))
		// remove all SocialNetworks then re-add them
		// Warning: This could be more efficient if it becomes a bottleneck
		n.getNodes.foreach(sn => sn.remove)
		employee.personalInfo.socialNetworks.foreach(sn => {
			val node = n.addNode(SocialNetwork.NodeName, SocialNetwork.NodeType)
			node.setProperty("name", sn.name)
			node.setProperty("url", sn.url)
		})
	}
	/** Fetch an Employee or User based on a given UUID or username
		* @param s - username or UUID by which a node will be fetched
		* @returns Employee or User built from node retrieved
		*/
	def get(s:String) = {
		val node = if (root.hasNode(s)) {
			root.getNode(s)
		} else {
			session.getNodeByUUID(s)
		}
		getByNode(node)
	}
	private def getByNode(node:Node) =
		node.getPrimaryNodeType.getName match {
			case Employee.NodeType => getEmployee(node)
			case User.NodeType => getCredentials(node)
		}
	/** Fetch employee from a given Node
		* @param node to build from
		* @returns Employee built from node
		*/
	private def getEmployee(node:Node) =
		Employee(
			new ContentInfo(
				node.getProperty("dateCreated").getDate,
				node.getProperty("lastModified").getDate,
				getCredentials(node.getProperty("modifiedBy").getNode)
			),
			getCredentials(node),
			PersonalInfo(
				node.getProperty("firstName").getString,
				node.getProperty("lastName").getString,
				node.getNodes.map(n => SocialNetwork(n.getProperty("name").getString,n.getProperty("url").getString)).toList
			),
			node.getUUID
		)
	/** Fetch credentials from a given Node so we don't fetch the whole user graph when retrieving Employee's modifiedBy
		* @param node to build from
		* @returns User built from uuid's node
		*/
	private def getCredentials(node:Node) =
		WkCredentials(
			node.getProperty("username").getString,
			node.getProperty("password").getString,
			node.getProperty("department").getString,
			node.getProperty("title").getString,
			Some(node.getUUID)
		)
}
