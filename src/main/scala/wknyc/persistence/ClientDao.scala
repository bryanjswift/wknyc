package wknyc.persistence

import javax.jcr.{Node,PropertyType,Session}
import wknyc.Config
import wknyc.model.{CaseStudy,Client,DownloadableAsset,ImageAsset,PressAsset,User}

class ClientDao(session:Session, loggedInUser:User) extends Dao(session,loggedInUser) {
	require(session.getWorkspace.getName == Config.ContentWorkspace,"Can only save/get Assets from ContentWorkspace")
	// Only retrieve root once
	override protected lazy val root = super.root
	// Root for Case Studies
	private lazy val CaseStudyRoot = getUnversionedNode("CaseStudys")
	// Root for Clients
	private lazy val ClientRoot = getUnversionedNode("Clients")
	// Need a way to (read only) access user data
	private lazy val security = Config.Repository.login(Config.Admin, Config.CredentialsWorkspace)
	protected override lazy val userDao = new UserDao(security,loggedInUser)
	// Need a way to (read only) access asset data
	private lazy val assetDao = new AssetDao(session,loggedInUser)
	def save(caseStudy:CaseStudy) = {
		val node = getNode(CaseStudyRoot,caseStudy.name,CaseStudy.NodeType)
		writeCaseStudy(node,caseStudy)
		session.save
		node.checkin
		caseStudy.cp(node.getUUID)
	}
	private def writeCaseStudy(node:Node,caseStudy:CaseStudy) {
		saveContentInfo(node,caseStudy.contentInfo.modifiedBy(loggedInUser))
		node.setProperty(CaseStudy.Name,caseStudy.name)
		node.setProperty(CaseStudy.Headline,caseStudy.headline)
		node.setProperty(CaseStudy.StudyType,caseStudy.studyType)
		node.setProperty(CaseStudy.Tags,caseStudy.tags)
		node.setProperty(CaseStudy.Related,caseStudy.related.map(a => a.uuid.get),PropertyType.REFERENCE)
		// child nodes
		assetDao.writeProperties(caseStudy.video,Some(node),CaseStudy.Video)
		assetDao.writeProperties(caseStudy.description,Some(node),CaseStudy.Description)
		val images = getUnversionedNode(node,CaseStudy.Images)
		val downloads = getUnversionedNode(node,CaseStudy.Downloads)
		val press = getUnversionedNode(node,CaseStudy.Press)
		caseStudy.images.foreach(i => assetDao.writeProperties(i,Some(images),i.title))
		caseStudy.downloads.foreach(d => assetDao.writeProperties(d,Some(downloads),d.title))
		caseStudy.press.foreach(p => assetDao.writeProperties(p,Some(press),p.title))
	}
	def getCaseStudy(uuid:String):CaseStudy = getCaseStudy(session.getNodeByUUID(uuid))
	private def getCaseStudy(node:Node):CaseStudy =
		CaseStudy(
			getContentInfo(node),
			node.getProperty(CaseStudy.Name).getString,
			node.getProperty(CaseStudy.Headline).getString,
			node.getProperty(CaseStudy.StudyType).getString,
			node.getProperty(CaseStudy.Tags).getValues.map(v => v.getString),
			node.getProperty(CaseStudy.Related).getValues.map(v => getCaseStudy(v.getString)),
			assetDao.getDownloadableAsset(node.getNode(CaseStudy.Video)),
			assetDao.getCopyAsset(node.getNode(CaseStudy.Description)),
			node.getNode(CaseStudy.Images).getNodes.map(n => assetDao.getImageAsset(n)),
			node.getNode(CaseStudy.Downloads).getNodes.map(n => assetDao.getDownloadableAsset(n)),
			node.getNode(CaseStudy.Press).getNodes.map(n => assetDao.getPressAsset(n))
		)
	def save(client:Client) = {
		val node = getNode(ClientRoot,client.name,Client.NodeType)
		saveContentInfo(node,client.contentInfo.modifiedBy(loggedInUser))
		node.setProperty(Client.Name,client.name)
		val caseStudies = getUnversionedNode(node,Client.CaseStudies)
		client.caseStudies.foreach(study => writeCaseStudy(getNode(caseStudies,study.name,CaseStudy.NodeType),study))
		session.save
		node.checkin
		client.cp(node.getUUID)
	}
	def getClient(uuid:String):Client = {
		val node = session.getNodeByUUID(uuid)
		val caseStudies = node.getNode(Client.CaseStudies)
		Client(
			getContentInfo(node),
			node.getProperty(Client.Name).getString,
			caseStudies.getNodes.map(n => getCaseStudy(n))
		)
	}
	// Release resources
	override def close = {
		assetDao.close
		security.logout
		userDao.close
	}
	// Implicitly convert List to Array so .toArray isn't everywhere
	private implicit def list2array[T](l:List[T]):Array[T] = l.toArray
	// Implicitly convert Array to List so .toList isn't everywhere
	private implicit def array2list[T](a:Array[T]):List[T] = a.toList
}
