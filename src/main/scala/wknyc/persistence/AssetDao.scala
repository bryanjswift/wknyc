package wknyc.persistence

import javax.jcr.{Node,Session}
import org.joda.time.DateTime
import scala.xml.XML
import wknyc.model.{Asset,Content,ContentInfo,CopyAsset,DownloadableAsset,FileInfo,Image,ImageAsset,PressAsset,User}

class AssetDao(session:Session, loggedInUser:User) extends Dao(session,loggedInUser) {
	require(session.getWorkspace.getName == Config.ContentWorkspace,"Can only save/get Assets from ContentWorkspace")
	// Need a way to (read only) access user data
	private def security = Config.Repository.login(Config.Admin, Config.CredentialsWorkspace)
	private lazy val userDao = new UserDao(security,loggedInUser)
	// Make the root for Asset saving a node called Assets
	override protected lazy val root = getUnversionedNode(super.root,"Assets")
	// Make the root for ImageAsset saving a node called ImageAssets
	private lazy val ImageRoot = getUnversionedNode("ImageAssets")
	// Get a root node for CopyAsset objects
	private lazy val CopyRoot = getUnversionedNode("CopyAssets")
	// Get a root node for DownloadAsset objects
	private lazy val DownloadableRoot = getUnversionedNode("DownloadableAssets")
	// Get a root node for PressAsset objects
	private lazy val PressRoot = getUnversionedNode("PressAssets")
	/** Save the provided asset in it's appropriate location
		* @param asset to be saved
		* @returns a copy of the asset with it's uuid updated
		*/
	def save[T <: Asset](asset:T):T = {
		val (root,nt) = asset match {
			case copy:CopyAsset => (CopyRoot,CopyAsset.NodeType)
			case download:DownloadableAsset => (DownloadableRoot,DownloadableAsset.NodeType)
			case image:ImageAsset => (ImageRoot,ImageAsset.NodeType)
			case press:PressAsset => (PressRoot,PressAsset.NodeType)
		}
		val node = getNode(root,asset.title,nt)
		writeProperties(node,asset)
		session.save
		node.checkin
		asset.cp(node.getUUID).asInstanceOf[T]
	}
	/** Write the properties of an Asset to the provided node based on it's type
		* @param node to write into
		* @asset to be written
		*/
	private def writeProperties[T <: Asset](node:Node,asset:T):Unit =
		asset match {
			case copy:CopyAsset => writeProperties(node,copy)
			case download:DownloadableAsset => writeProperties(node,download)
			case image:ImageAsset => writeProperties(node,image)
			case press:PressAsset => writeProperties(node,press)
		}
	/** Write general Asset properties to a node
		* @param node to write into
		* @param asset to be written
		*/
	private def writeAssetProperties(node:Node,asset:Asset) = {
		node.setProperty(Asset.Title,asset.title)
		node.setProperty(Content.DateCreated,asset.contentInfo.dateCreated)
		node.setProperty(Content.LastModified,asset.contentInfo.lastModified)
		node.setProperty(Content.ModifiedBy, loggedInUser.uuid.get)
	}
	/** Write general FileInfo properties to a node
		* @param node to write into
		* @param file to be written
		*/
	private def writeFileInfoProperties(node:Node,file:FileInfo) = {
		node.setProperty(FileInfo.Path,file.path)
		node.setProperty(FileInfo.Url,file.url)
	}
	/** Write properties of a CopyAsset to a node
		* @param node to write
		* @param copy asset holding data
		*/
	private def writeProperties(node:Node,copy:CopyAsset) = {
		writeAssetProperties(node,copy)
		node.setProperty(CopyAsset.Body,copy.body.toString)
	}
	/** Write properties of a DownloadableAsset to a node
		* @param node to write
		* @param download asset holding data
		*/
	private def writeProperties(node:Node,download:DownloadableAsset) = {
		writeAssetProperties(node,download)
		writeFileInfoProperties(node, download)
	}
	/** Write properties of an ImageAsset to a node
		* @param node to write to
		* @param image asset holding data to write
		*/
	private def writeProperties(node:Node,image:ImageAsset) = {
		writeAssetProperties(node,image)
		image.images.foreach(info => {
			val n = getUnversionedNode(node,info.size.name,Image.NodeType)
			writeFileInfoProperties(n, info)
			n.setProperty(Image.Alt, info.alt)
			n.setProperty(Image.Height, info.height)
			n.setProperty(Image.Width, info.width)
		})
	}
	/** Write properties of a PressAsset to a node
		* @param node to write into
		* @param press asset holding data
		*/
	private def writeProperties(node:Node,press:PressAsset) = {
		writeAssetProperties(node,press)
		node.setProperty(PressAsset.Author,press.author)
		node.setProperty(PressAsset.Source,press.source)
		node.setProperty(PressAsset.SourceName,press.sourceName)
	}
	def get(uuid:String) = getByNode(session.getNodeByUUID(uuid))
	private def getByNode(node:Node) =
		node.getPrimaryNodeType.getName match {
			case CopyAsset.NodeType => getCopyAsset(node)
			case DownloadableAsset.NodeType => getDownloadableAsset(node)
			case ImageAsset.NodeType => getImageAsset(node)
			case PressAsset.NodeType => getPressAsset(node)
		}
	private def getContentInfo(node:Node) =
		new ContentInfo(
			node.getProperty(Content.DateCreated).getDate,
			node.getProperty(Content.LastModified).getDate,
			userDao.get(node.getProperty(Content.ModifiedBy).getString),
			Some(node.getUUID)
		)
	private def getCopyAsset(node:Node) =
		CopyAsset(
			getContentInfo(node),
			node.getProperty(Asset.Title).getString,
			XML.loadString(node.getProperty(CopyAsset.Body).getString)
		)
	private def getDownloadableAsset(node:Node) =
		DownloadableAsset(
			getContentInfo(node),
			node.getProperty(Asset.Title).getString,
			node.getProperty(FileInfo.Url).getString,
			node.getProperty(FileInfo.Path).getString
		)
	private def getImageAsset(node:Node) = {
		import wknyc.model.{ImageInfo,ImageSet,ImageSize}
		val images = node.getNodes.foldLeft(Map[ImageSize,ImageInfo]())((map,node) =>
			map + (ImageSize(node.getName) -> Image(
					node.getProperty(FileInfo.Path).getString,
					node.getProperty(FileInfo.Url).getString,
					node.getProperty(Image.Alt).getString,
					node.getProperty(Image.Width).getLong.toInt,
					node.getProperty(Image.Height).getLong.toInt,
					ImageSize(node.getName)
				)
			)
		)
		ImageAsset(
			getContentInfo(node),
			node.getProperty(Asset.Title).getString,
			ImageSet(images)
		)
	}
	private def getPressAsset(node:Node) =
		PressAsset(
			getContentInfo(node),
			node.getProperty(Asset.Title).getString,
			node.getProperty(PressAsset.Author).getString,
			node.getProperty(PressAsset.Source).getString,
			node.getProperty(PressAsset.SourceName).getString
		)
}
