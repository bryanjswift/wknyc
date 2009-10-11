package wknyc.persistence

import javax.jcr.{Node,Session}
import wknyc.model.{Asset,Content,Image,ImageAsset,User}

class AssetDao(session:Session, loggedInUser:User) extends Dao(session,loggedInUser) {
	require(session.getWorkspace.getName == Config.ContentWorkspace,"Can only save/get Assets from ContentWorkspace")
	// Make the root for Asset saving a node called Assets
	override protected lazy val root = getNode('Assets.toString)
	// Make the root for ImageAsset saving a node called ImageAssets
	private lazy val ImageRoot = getNode('ImageAssets.toString)
	/** Save the provided asset in it's appropriate location
		* @param asset to be saved
		* @returns a copy of the asset with it's uuid updated
		*/
	def save[T <: Asset](asset:T):T =
		asset match {
			case image:ImageAsset => saveImageAsset(image).asInstanceOf[T]
		}
	/** Save an ImageAsset
		* @param image asset to save
		* @returns a copy of the ImageAsset with the uuid updated (if new)
		*/
	private def saveImageAsset(image:ImageAsset) = {
		val node = getNode(ImageRoot,image.title,ImageAsset.NodeType)
		writeProperties(node,image)
		session.save
		node.checkin
		image.cp(node.getUUID)
	}
	/** Write properties of an ImageAsset to a node
		* @param node to write to
		* @param image asset holding data to write
		*/
	private def writeProperties(node:Node,image:ImageAsset) = {
		image.images.foreach(info => {
			val n = getNode(node,info.size.name,Image.NodeType)
			n.setProperty(FileInfo.Path, info.path)
			n.setProperty(FileInfo.Url, info.url)
			n.setProperty(Image.Alt, info.alt)
			n.setProperty(Image.Height, info.height)
			n.setProperty(Image.Width, info.width)
		})
		node.setProperty(Asset.Title,image.title)
		node.setProperty(Content.DateCreated,image.contentInfo.dateCreated)
		node.setProperty(Content.LastModified,image.contentInfo.lastModified)
		node.setProperty(Content.ModifiedBy, session.getNodeByUUID(loggedInUser.uuid.get))
	}
}
