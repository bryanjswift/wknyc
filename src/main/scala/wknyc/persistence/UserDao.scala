package wknyc.persistence

import javax.jcr.{Node,Session}
import wknyc.Config
import wknyc.model.{Content,ContentInfo,Employee,PersonalInfo,SocialNetwork,User,UserRole,WkCredentials}

/** UserDao is created to save and retrieve User type objects from the repository
	* @param session is used to access the repository but is not modified (logged out)
	* @param loggedInUser is used to set lastModifiedUser of content being saved
	*/
class UserDao(loggedInUser:User) extends Dao(loggedInUser) {
	protected val session = Config.Repository.login(loggedInUser,Config.CredentialsWorkspace)
	// Only retrieve root once
	override protected lazy val root = super.root
	// Make userDao refer to this
	override protected lazy val userDao = this
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
			credentials.role,
			credentials.title,
			Some(n.getUUID)
		)
	}
	/** Save an Employee to the repository
		* @param employee to be saved
		* @returns Employee with uuid, lastModified and modifiedBy updated
		*/
	private def saveEmployee(employee:Employee) = {
		val ci = employee.contentInfo.modifiedBy(loggedInUser) // do it first so saved data and object have same values
		val n = getNode(employee.username,Employee.NodeType)
		writeProperties(n,employee,ci)
		session.save
		n.checkin
		Employee(
			ci.cp(n.getUUID),
			// TODO: In Scala 2.8.0 change this to use copy(uuid = Some(n.getUUID))
			employee.credentials.cp(n.getUUID),
			employee.personalInfo
		)
	}
	/** Write general user properties to provided node
		* @param n - node to write data to
		* @param user whose data should be written
		*/
	private def writeProperties(n:Node,user:User) {
		n.setProperty(User.Username,user.username)
		n.setProperty(User.Password,user.password)
		n.setProperty(User.Role,user.role.id)
		n.setProperty(User.Title,user.title)
	}
	/** Write employee information to provided node
		* @param n - node to write data to
		* @param employee object containing data to write
		* @param ci ContentInfo with datestamps and user tracking to write
		*/
	private def writeProperties(n:Node,employee:Employee,ci:ContentInfo) {
		writeProperties(n,employee)
		n.setProperty(Employee.FirstName,employee.firstName)
		n.setProperty(Employee.LastName,employee.lastName)
		saveContentInfo(n,employee.contentInfo.modifiedBy(loggedInUser))
		// remove all SocialNetworks then re-add them
		// Warning: This could be more efficient if it becomes a bottleneck
		n.getNodes.foreach(sn => sn.remove)
		employee.personalInfo.socialNetworks.foreach(sn => {
			val node = n.addNode(SocialNetwork.NodeName, SocialNetwork.NodeType)
			node.setProperty(SocialNetwork.Name, sn.name)
			node.setProperty(SocialNetwork.Url, sn.url)
		})
	}
	/** Check whether a given username exists in the repository
		* @param username to check for
		* @returns true if username is taken, false otherwise
		*/
	def exists(username:String):Boolean = root.hasNode(username)
	/** Fetch an Employee or User based on a given UUID or username
		* @param s - username or UUID by which a node will be fetched
		* @returns Employee or User built from node retrieved
		*/
	def get(s:String):User = {
		val node =
			if (root.hasNode(s)) {
				root.getNode(s)
			} else {
				session.getNodeByUUID(s)
			}
		getByNode(node)
	}
	/** Get appropriate object based on primary node type of given node
		* @param node to retrieve from
		* @returns WkCredentials or Employee depending on node type
		*/
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
			getContentInfo(node),
			getCredentials(node),
			PersonalInfo(
				node.getProperty(Employee.FirstName).getString,
				node.getProperty(Employee.LastName).getString,
				node.getNodes.map(
					n => SocialNetwork(n.getProperty(SocialNetwork.Name).getString,n.getProperty(SocialNetwork.Url).getString)
				).toList
			)
		)
	/** Fetch credentials from a given Node so we don't fetch the whole user graph when retrieving Employee's modifiedBy
		* @param node to build from
		* @returns User built from uuid's node
		*/
	private[persistence] def getCredentials(node:Node) =
		WkCredentials(
			node.getProperty(User.Username).getString,
			node.getProperty(User.Password).getString,
			UserRole(node.getProperty(User.Role).getLong),
			node.getProperty(User.Title).getString,
			Some(node.getUUID)
		)
	// Release resources
	override def close = {
		session.logout
	}
}
