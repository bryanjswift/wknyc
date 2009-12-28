package wknyc.persistence

import javax.jcr.{Node,Session}
import wknyc.Config
import wknyc.model.{Client,User}

class ClientDao(loggedInUser:User) extends Dao(loggedInUser) {
	protected val session = Config.Repository.login(loggedInUser,Config.ContentWorkspace)
	// Only retrieve root once
	override protected lazy val root = super.root
	// Root for Clients
	private lazy val ClientRoot = getNode("Clients")
	// Need a way to (read only) access user data
	protected override lazy val userDao = new UserDao(loggedInUser)
	// Need a way to read/write CaseStudy data
	private lazy val caseStudyDao = new CaseStudyDao(loggedInUser)
	def get(uuid:String):Client = get(session.getNodeByUUID(uuid))
	private def get(node:Node):Client = get(node,false)
	/** Get Client data with or without CaseStudy data
		* @param node from whence the data shall be retrieved
		* @param studies - whether or not to load CaseStudy data
		*/
	private def get(node:Node,studies:Boolean):Client =
		Client(
			getContentInfo(node),
			node.getProperty(Client.Name).getString,
			if (studies) Nil else getCaseStudies(node)
		)
	private def getCaseStudies(node:Node) =
		node.getReferences.map(prop => caseStudyDao.get(prop.getParent))
	def list =
		ClientRoot.getNodes.map(n => get(n,true))
	def save(client:Client) = {
		val node = getNode(ClientRoot,client.name,Client.NodeType,client)
		saveContentInfo(node,client.contentInfo.modifiedBy(loggedInUser))
		node.setProperty(Client.Name,client.name)
		session.save
		val updatedClient = client.cp(Some(node.getUUID)) 
		client.caseStudies.foreach(study => {
			caseStudyDao.save(study.cp(updatedClient))
		})
		node.checkin
		updatedClient
	}
	// Release resources
	def close {
		caseStudyDao.close
		session.logout
		userDao.close
	}
}
