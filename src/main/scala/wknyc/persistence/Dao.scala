package wknyc.persistence

import javax.jcr.{Node,NodeIterator,Session}
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,User,WkCredentials}

/** UserDao is created to save and retrieve User type objects from the repository
	* @param session is used to access the repository but is not modified (logged out)
	* @param loggedInUser is used to set lastModifiedUser of content being saved
	*/
class UserDao(private val session:Session, private val loggedInUser:User) {
	/** Save an object which is at least of type User
		* @param user to be saved delegates to saveCredentials or saveEmployee depending on type
		* @returns T with uuid populated and lastModified/modifiedBy fields updated (if applicable)
		*/
	def save[T >: User](user:T):T =
		user match {
			case employee:Employee => saveEmployee(employee)
			case credentials:WkCredentials => saveCredentials(credentials)
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
	/** Create/retrieve a referenceable and versionable node from the root of the session's workspace
		* @param name of the node to retrieve
		* @param nt - type of node to retrieve
		* @returns Node with given name (as path)
		*/
	private def getNode(name:String,nt:String):Node = getNode(session.getRootNode,name,nt)
	/** Create/retrieve a referenceable and versionable node from the given parent node
		* @param parent node to search from
		* @param name of the node to retrieve
		* @param nt - type of node to retrieve
		* @returns Node with given name (as path)
		*/
	private def getNode(parent:Node,name:String,nt:String):Node =
		if (parent.hasNode(name)) {
			val node = parent.getNode(name)
			node.checkout
			node
		} else {
			parent.addNode(name, nt)
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
	}
	/** Fetch an Employee based on a given UUID
		* @param uuid of data to be fetched
		* @returns Employee built from data in uuid's node
		*/
	def getById(uuid:String) = {
		val n = session.getNodeByUUID(uuid)
		Employee(
			new ContentInfo(
				n.getProperty("dateCreated").getDate,
				n.getProperty("lastModified").getDate,
				getUser(n.getProperty("modifiedBy").getNode.getUUID)
			),
			WkCredentials(
				n.getProperty("username").getString,
				n.getProperty("password").getString,
				n.getProperty("department").getString,
				n.getProperty("title").getString,
				Some(uuid)
			),
			PersonalInfo(
				n.getProperty("firstName").getString,
				n.getProperty("lastName").getString,
				List[SocialNetwork]()
			),
			uuid
		)
	}
	/** Fetch a simple user from a given UUID so we don't fetch the whole user graph when retrieving Employee's modifiedBy
		* @param uuid of data to fetch
		* @returns User built from uuid's node
		*/
	private def getUser(uuid:String) = {
		val n = session.getNodeByUUID(uuid)
		new User {
			val credentials:WkCredentials = null // I don't like this
			override val username = n.getProperty("username").getString
			override val password = n.getProperty("password").getString
			override val department = n.getProperty("department").getString
			override val title = n.getProperty("title").getString
			val uuid = Some(n.getUUID)
		}
	}
	// Convert a NodeIterator to an actual Iterator with generics
	implicit def nodeiterator2iterator(nodeIterator:NodeIterator):Iterator[Node] = new Iterator[Node] {
		def hasNext = nodeIterator.hasNext
		def next = nodeIterator.nextNode
	}
}
