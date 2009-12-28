package wknyc.persistence

import javax.jcr.{Node,Session}
import scala.xml.XML
import wknyc.Config
import wknyc.model.{Asset,AwardAsset,Content,ContentInfo,CopyAsset,DownloadableAsset,FileInfo,Image,ImageInfo,ImageAsset,PressAsset,User}

class AssetDao(loggedInUser:User) extends Dao(loggedInUser) {
	protected val session = Config.Repository.login(loggedInUser,Config.ContentWorkspace)
	// Need a way to (read only) access user data
	protected override lazy val userDao = new UserDao(loggedInUser)
	// Make the root for Asset saving a node called Assets
	override protected lazy val root = getNode(super.root,"Assets")
	// Make the root for ImageAsset saving a node called ImageAssets
	private lazy val ImageRoot = getNode("ImageAssets")
	// Get a root node for CopyAsset objects
	private lazy val CopyRoot = getNode("CopyAssets")
	// Get a root node for DownloadAsset objects
	private lazy val DownloadableRoot = getNode("DownloadableAssets")
	// Get a root node for PressAsset objects
	private lazy val PressRoot = getNode("PressAssets")
	// Get a root node for AwardAsset objects
	private lazy val AwardRoot = getNode("AwardAssets")
	/** Save the provided asset in it's appropriate location
		* @param asset to be saved
		* @returns a copy of the asset with it's uuid updated
		*/
	def save[T <: Asset](asset:T):T = {
		val node = writeProperties(asset,None,asset.title)
		session.save
		node.checkin
		asset.cp(Some(node.getUUID)).asInstanceOf[T]
	}
	private[persistence] def writeProperties[T <: Asset](asset:T,root:Option[Node],name:String):Node =
		asset match {
			case copy:CopyAsset =>
				writeProperties(getNode(root.getOrElse(CopyRoot),name,CopyAsset.NodeType),copy)
			case download:DownloadableAsset =>
				writeProperties(getNode(root.getOrElse(DownloadableRoot),name,DownloadableAsset.NodeType),download)
			case image:ImageAsset =>
				writeProperties(getNode(root.getOrElse(ImageRoot),name,ImageAsset.NodeType),image)
			case press:PressAsset =>
				writeProperties(getNode(root.getOrElse(PressRoot),name,PressAsset.NodeType),press)
			case award:AwardAsset =>
				writeProperties(getNode(root.getOrElse(AwardRoot),name,AwardAsset.NodeType),award)
			case null =>
				null
		}
	/** Write general Asset properties to a node
		* @param node to write into
		* @param asset to be written
		*/
	private def writeAssetProperties(node:Node,asset:Asset) = {
		node.setProperty(Asset.Title,asset.title)
		saveContentInfo(node,asset.contentInfo.modifiedBy(loggedInUser))
		node
	}
	/** Write general FileInfo properties to a node
		* @param node to write into
		* @param file to be written
		*/
	private def writeFileInfoProperties(node:Node,file:FileInfo) = {
		node.setProperty(FileInfo.Path,file.path)
		node.setProperty(FileInfo.Url,file.url)
	}
	/** Write ImageInfo properties to a node
		* @param node to write into
		* @param ImageInfo to be written
		*/
	private def writeImageProperties(parent:Node,info:ImageInfo) = {
		val n = getNode(parent,info.size.name,Image.NodeType)
		writeFileInfoProperties(n, info)
		n.setProperty(Image.Alt, info.alt)
		n.setProperty(Image.Height, info.height)
		n.setProperty(Image.Width, info.width)
	}
	/** Write general AwardAsset properties to a node
		* @param node to write into
		* @param award to be written
		*/
	private def writeProperties(node:Node,award:AwardAsset) = {
		node.setProperty(AwardAsset.Description,session.getNodeByUUID(award.description.uuid.get))
		node.setProperty(AwardAsset.Image,session.getNodeByUUID(award.image.uuid.get))
		node.setProperty(AwardAsset.Source,award.source)
		writeAssetProperties(node,award)
	}
	/** Write properties of a CopyAsset to a node
		* @param node to write
		* @param copy asset holding data
		*/
	private def writeProperties(node:Node,copy:CopyAsset) = {
		node.setProperty(CopyAsset.Body,copy.body.toString)
		writeAssetProperties(node,copy)
	}
	/** Write properties of a DownloadableAsset to a node
		* @param node to write
		* @param download asset holding data
		*/
	private def writeProperties(node:Node,download:DownloadableAsset) = {
		writeFileInfoProperties(node, download)
		writeAssetProperties(node,download)
	}
	/** Write properties of an ImageAsset to a node
		* @param node to write to
		* @param image asset holding data to write
		*/
	private def writeProperties(node:Node,image:ImageAsset) = {
		image.images.foreach(info => writeImageProperties(node,info))
		writeAssetProperties(node,image)
	}
	/** Write properties of a PressAsset to a node
		* @param node to write into
		* @param press asset holding data
		*/
	private def writeProperties(node:Node,press:PressAsset) = {
		node.setProperty(PressAsset.Author,press.author)
		node.setProperty(PressAsset.Source,press.source)
		node.setProperty(PressAsset.SourceName,press.sourceName)
		writeAssetProperties(node,press)
	}
	/** Retrieve an Asset by uuid
		* @param uuid of asset to retrieve
		* @returns Asset corresponding to uuid
		*/
	def get[T <: Asset](uuid:String):T = getByNode(session.getNodeByUUID(uuid)).asInstanceOf[T]
	/** Get appropriate object based on primary node type of given node
		* @param node to retrieve from
		* @returns Asset depending on node type
		*/
	private def getByNode(node:Node) =
		node.getPrimaryNodeType.getName match {
			case AwardAsset.NodeType => getAwardAsset(node)
			case CopyAsset.NodeType => getCopyAsset(node)
			case DownloadableAsset.NodeType => getDownloadableAsset(node)
			case ImageAsset.NodeType => getImageAsset(node)
			case PressAsset.NodeType => getPressAsset(node)
		}
	private[persistence] def getAwardAsset(node:Node) =
		AwardAsset(
			getContentInfo(node),
			node.getProperty(Asset.Title).getString,
			node.getProperty(AwardAsset.Source).getString,
			getCopyAsset(node.getProperty(AwardAsset.Description).getNode),
			getImageAsset(node.getProperty(AwardAsset.Image).getNode)
		)
	private[persistence] def getCopyAsset(node:Node) =
		CopyAsset(
			getContentInfo(node),
			node.getProperty(Asset.Title).getString,
			XML.loadString(node.getProperty(CopyAsset.Body).getString)
		)
	private[persistence] def getDownloadableAsset(node:Node) =
		DownloadableAsset(
			getContentInfo(node),
			node.getProperty(Asset.Title).getString,
			node.getProperty(FileInfo.Path).getString,
			node.getProperty(FileInfo.Url).getString
		)
	private def getImage(node:Node) = {
		import wknyc.model.ImageSize
		Image(
			node.getProperty(FileInfo.Path).getString,
			node.getProperty(FileInfo.Url).getString,
			node.getProperty(Image.Alt).getString,
			ImageSize(node.getName)
		)
	}
	private[persistence] def getImageAsset(node:Node) = {
		import wknyc.model.{ImageSet,ImageSize}
		val images = node.getNodes.foldLeft(Map[ImageSize,ImageInfo]())((map,node) =>
			map + (ImageSize(node.getName) -> getImage(node))
		)
		ImageAsset(
			getContentInfo(node),
			node.getProperty(Asset.Title).getString,
			ImageSet(images)
		)
	}
	private[persistence] def getPressAsset(node:Node) =
		PressAsset(
			getContentInfo(node),
			node.getProperty(Asset.Title).getString,
			node.getProperty(PressAsset.Author).getString,
			node.getProperty(PressAsset.Source).getString,
			node.getProperty(PressAsset.SourceName).getString
		)
	// Release resources
	override def close = {
		session.logout
		userDao.close
	}
}
