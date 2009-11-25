package wknyc.persistence

import javax.jcr.{Node,Session}
import wknyc.Config
import wknyc.model.{CaseStudy,Client,EmptyFile,Ordered,User}

class CaseStudyDao(loggedInUser:User) extends Dao(loggedInUser) {
	protected val session = Config.Repository.login(loggedInUser,Config.ContentWorkspace)
	// Only retrieve root once
	override protected lazy val root = super.root
	// Need a way to (read only) access user data
	protected override lazy val userDao = new UserDao(loggedInUser)
	// Need a way to read/write access asset data
	private lazy val assetDao = new AssetDao(loggedInUser)
	def get(uuid:String):CaseStudy = get(session.getNodeByUUID(uuid))
	private[persistence] def get(node:Node):CaseStudy =
		CaseStudy(
			getContentInfo(node),
			getClient(node.getProperty(CaseStudy.Client).getNode), // get client without list of studies
			node.getProperty(CaseStudy.Name).getString,
			node.getProperty(CaseStudy.Launch).getDate,
			node.getProperty(CaseStudy.Headline).getString,
			node.getProperty(CaseStudy.Description).getString,
			node.getNode(CaseStudy.Downloads).getNodes.map(n => assetDao.getDownloadableAsset(n)),
			node.getProperty(CaseStudy.Published).getBoolean,
			node.getProperty(Ordered.Position).getLong,
			if (node.hasNode(CaseStudy.Video)) { assetDao.getDownloadableAsset(node.getNode(CaseStudy.Video)) }
			else { EmptyFile },
			node.getNode(CaseStudy.Images).getNodes.map(n => assetDao.getImageAsset(n)),
			node.getNode(CaseStudy.Press).getNodes.map(n => assetDao.getPressAsset(n))
		)
	/** TODO: Dirty nastiness to avoid dependency on ClientDao
		* Get Client data with or without CaseStudy data
		* @param node from whence the data shall be retrieved
		* @param studies - whether or not to load CaseStudy data
		*/
	private def getClient(node:Node) =
		Client(
			getContentInfo(node),
			node.getProperty(Client.Name).getString,
			Nil
		)
	/** Persist the CaseStudy to the repository
		* If caseStudy.client has not been persisted this will throw an Exception
		* @param caseStudy - instance of CaseStudy trait to save
		* @returns caseStudy with uuid updated
		*/
	def save(caseStudy:CaseStudy) = {
		val client = session.getNodeByUUID(caseStudy.client.uuid.get)
		client.checkout
		val caseStudies = getNode(client,Client.CaseStudies)
		val node = writeCaseStudy(caseStudies,client,caseStudy)
		client.save
		client.checkin
		caseStudy.cp(node.getUUID)
	}
	private[persistence] def writeCaseStudy(parent:Node,client:Node,caseStudy:CaseStudy) = {
		val node = getNode(parent,caseStudy.name,CaseStudy.NodeType)
		saveContentInfo(node,caseStudy.contentInfo.modifiedBy(loggedInUser))
		node.setProperty(CaseStudy.Client,client)
		node.setProperty(CaseStudy.Name,caseStudy.name)
		node.setProperty(CaseStudy.Headline,caseStudy.headline)
		node.setProperty(CaseStudy.Description,caseStudy.description)
		node.setProperty(CaseStudy.Launch,caseStudy.launch)
		node.setProperty(CaseStudy.Published,caseStudy.published)
		node.setProperty(Ordered.Position,caseStudy.position)
		// child nodes
		val images = getNode(node,CaseStudy.Images)
		val downloads = getNode(node,CaseStudy.Downloads)
		val press = getNode(node,CaseStudy.Press)
		assetDao.writeProperties(caseStudy.video,Some(node),CaseStudy.Video)
		caseStudy.downloads.foreach(d => assetDao.writeProperties(d,Some(downloads),d.title))
		caseStudy.images.foreach(i => assetDao.writeProperties(i,Some(images),i.title))
		caseStudy.press.foreach(p => assetDao.writeProperties(p,Some(press),p.title))
		node
	}
	// Release resources
	override def close {
		assetDao.close
		session.logout
		userDao.close
	}
}
