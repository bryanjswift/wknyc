package wknyc.persistence

import javax.jcr.{PropertyType,Session}
import wknyc.model.{CaseStudy,Client,DownloadableAsset,PressAsset,User}

class ClientDao(session:Session, loggedInUser:User) extends Dao(session,loggedInUser) {
	require(session.getWorkspace.getName == Config.ContentWorkspace,"Can only save/get Assets from ContentWorkspace")
	// Only retrieve root once
	override protected lazy val root = super.root
	// Root for Case Studies
	private lazy val CaseStudyRoot = getUnversionedNode("CaseStudys")
	// Root for Clients
	private lazy val ClientRoot = getUnversionedNode("Clients")
	// Need a way to (read only) access user data
	private def security = Config.Repository.login(Config.Admin, Config.CredentialsWorkspace)
	protected override lazy val userDao = new UserDao(security,loggedInUser)
	private lazy val assetDao = new AssetDao(session,loggedInUser)
	def save(caseStudy:CaseStudy) = {
		val node = getNode(CaseStudyRoot,caseStudy.name,CaseStudy.NodeType)
		saveContentInfo(node,caseStudy.contentInfo.modify(loggedInUser))
		node.setProperty(CaseStudy.Description,session.getNodeByUUID(caseStudy.description.uuid.get))
		node.setProperty(CaseStudy.Downloads,caseStudy.downloads.map(a => a.uuid.get),PropertyType.REFERENCE)
		node.setProperty(CaseStudy.Headline,caseStudy.headline)
		node.setProperty(CaseStudy.Name,caseStudy.name)
		node.setProperty(CaseStudy.Press,caseStudy.press.map(a => a.uuid.get),PropertyType.REFERENCE)
		node.setProperty(CaseStudy.Related,caseStudy.related.map(a => a.uuid.get),PropertyType.REFERENCE)
		node.setProperty(CaseStudy.StudyType,caseStudy.studyType)
		node.setProperty(CaseStudy.Tags,caseStudy.tags)
		session.save
		node.checkin
		caseStudy.cp(node.getUUID)
	}
	def getCaseStudy(uuid:String):CaseStudy = {
		val node = session.getNodeByUUID(uuid)
		CaseStudy(
			getContentInfo(node),
			assetDao.getCopyAsset(node.getProperty(CaseStudy.Description).getNode),
			node.getProperty(CaseStudy.Downloads).getValues.map(v => assetDao.get[DownloadableAsset](v.getString)),
			node.getProperty(CaseStudy.Headline).getString,
			node.getProperty(CaseStudy.Name).getString,
			node.getProperty(CaseStudy.Press).getValues.map(v => assetDao.get[PressAsset](v.getString)),
			node.getProperty(CaseStudy.Related).getValues.map(v => getCaseStudy(v.getString)),
			node.getProperty(CaseStudy.StudyType).getString,
			node.getProperty(CaseStudy.Tags).getValues.map(v => v.getString)
		)
	}
	def save(client:Client) = {
		val node = getNode(ClientRoot,client.name,Client.NodeType)
		saveContentInfo(node,client.contentInfo.modify(loggedInUser))
		node.setProperty(Client.Name,client.name)
		node.setProperty(Client.CaseStudies,client.caseStudies.map(study => study.uuid.get),PropertyType.REFERENCE)
		session.save
		node.checkin
		client.cp(node.getUUID)
	}
	def getClient(uuid:String):Client = {
		val node = session.getNodeByUUID(uuid)
		Client(
			getContentInfo(node),
			node.getProperty(Client.Name).getString,
			node.getProperty(Client.CaseStudies).getValues.map(v => getCaseStudy(v.getString))
		)
	}
	// Release resources
	override def close = userDao.close
	// Implicitly convert List to Array so .toArray isn't everywhere
	private implicit def list2array[T](l:List[T]):Array[T] = l.toArray
	// Implicitly convert Array to List so .toList isn't everywhere
	private implicit def array2list[T](a:Array[T]):List[T] = a.toList
}
