package wknyc.persistence

import javax.jcr.{Node,Session}
import wknyc.Config
import wknyc.model.{AssetCaseStudy,BasicCaseStudy,CaseStudy,EmptyFile,Ordered,User}

class CaseStudyDao(session:Session, loggedInUser:User) extends Dao(session,loggedInUser) {
	require(session.getWorkspace.getName == Config.ContentWorkspace,"Can only save/get Assets from ContentWorkspace")
	// Only retrieve root once
	override protected lazy val root = super.root
	// Root for Case Studies
	private lazy val CaseStudyRoot = getNode("CaseStudys")
	// Need a way to (read only) access user data
	private lazy val security = Config.Repository.login(Config.Admin, Config.CredentialsWorkspace)
	protected override lazy val userDao = new UserDao(security,loggedInUser)
	// Need a way to (read only) access asset data
	private lazy val assetDao = new AssetDao(session,loggedInUser)
	def get(uuid:String):AssetCaseStudy = get(session.getNodeByUUID(uuid))
	private[persistence] def get(node:Node):AssetCaseStudy =
		AssetCaseStudy(
			BasicCaseStudy(
				getContentInfo(node),
				node.getProperty(CaseStudy.Name).getString,
				node.getProperty(CaseStudy.Headline).getString,
				node.getProperty(CaseStudy.Description).getString,
				node.getProperty(CaseStudy.Launch).getDate,
				node.getNode(CaseStudy.Downloads).getNodes.map(n => assetDao.getDownloadableAsset(n)),
				node.getProperty(CaseStudy.Published).getBoolean,
				node.getProperty(Ordered.Position).getLong
			),
			if (node.hasNode(CaseStudy.Video)) { assetDao.getDownloadableAsset(node.getNode(CaseStudy.Video)) } else { EmptyFile },
			node.getNode(CaseStudy.Images).getNodes.map(n => assetDao.getImageAsset(n)),
			node.getNode(CaseStudy.Press).getNodes.map(n => assetDao.getPressAsset(n))
		)
	/** Persist the CaseStudy to the repository
		* If caseStudy.client has not been persisted this will throw an Exception
		* @param caseStudy - instance of CaseStudy trait to save
		* @returns caseStudy with uuid updated
		*/
	def save(caseStudy:CaseStudy) = {
		//val client = session.getNodeByUUID(caseStudy.client.uuid.get)
		val node = getNode(CaseStudyRoot,caseStudy.name,CaseStudy.NodeType)
		writeCaseStudy(node,caseStudy)
		session.save
		node.checkin
		caseStudy.cp(node.getUUID)
	}
	private[persistence] def writeCaseStudy(node:Node,caseStudy:CaseStudy) {
		caseStudy match {
			case assets:AssetCaseStudy => writeAssetCaseStudy(node,assets)
			case _ => writeBasicCaseStudy(node,caseStudy)
		}
	}
	private def writeBasicCaseStudy(node:Node,caseStudy:CaseStudy) {
		saveContentInfo(node,caseStudy.contentInfo.modifiedBy(loggedInUser))
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
		caseStudy.downloads.foreach(d => assetDao.writeProperties(d,Some(downloads),d.title))
	}
	private def writeAssetCaseStudy(node:Node,caseStudy:AssetCaseStudy) {
		writeBasicCaseStudy(node,caseStudy)
		val images = getNode(node,CaseStudy.Images)
		val press = getNode(node,CaseStudy.Press)
		assetDao.writeProperties(caseStudy.video,Some(node),CaseStudy.Video)
		caseStudy.images.foreach(i => assetDao.writeProperties(i,Some(images),i.title))
		caseStudy.press.foreach(p => assetDao.writeProperties(p,Some(press),p.title))
	}
	// Release resources
	override def close {
		assetDao.close
		security.logout
		userDao.close
	}
}
