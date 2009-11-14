package wknyc.persistence

import javax.jcr.{Node,NodeIterator,Session}
import wknyc.model.{Content,ContentInfo,User}

abstract class Dao(private val session:Session, private val loggedInUser:User) {
	// Root node for this Dao
	protected def root = session.getRootNode
	// Dao with access to user data
	protected def userDao:UserDao
	/** Release resources associated with the Dao
		*/
	def close
	/**Delete node with the given UUID
		* @param uuid of the node to delete
		*/
	def delete(uuid:String) = {
		val node = session.getNodeByUUID(uuid)
		val parent = node.getParent
		val versionable = parent.isNodeType("mix:versionable")
		if (versionable) parent.checkout
		node.remove
		parent.save
		if (versionable) parent.checkin
	}
	/** Create/retrieve an unstructured node from the root of the session's workspace
		* @param name of the node to retrieve
		* @returns Node with the given name
		*/
	protected def getNode(name:String):Node = getNode(root,name,Dao.DefaultNodeType)
	/** Create/retrieve an unstructured node from the root of the session's workspace
		* @param parent node to search from
		* @param name of the node to retrieve
		* @returns Node with the given name
		*/
	protected def getNode(parent:Node,name:String):Node = getNode(parent,name,Dao.DefaultNodeType)
	/** Create/retrieve a referenceable and versionable node from the root of the session's workspace
		* @param name of the node to retrieve
		* @param nt - type of node to retrieve
		* @returns Node with given name (as path)
		*/
	protected def getNode(name:String,nt:String):Node = getNode(root,name,nt)
	/** Create/retrieve a referenceable and versionable node from the given parent node
		* @param parent node to search from
		* @param name of the node to retrieve
		* @param nt - type of node to retrieve
		* @returns Node with given name (as path)
		*/
	protected def getNode(parent:Node,name:String,nt:String):Node =
		if (parent.hasNode(name)) {
			val node = parent.getNode(name)
			node.checkout
			node
		} else {
			parent.addNode(name, nt)
		}
	/** Shortcut to retrieve a node from root by supplying only name
		* @param name of the node to retrieve
		* @returns Node with given name (as path)
		*/
	protected def getUnversionedNode(name:String):Node = getUnversionedNode(root,name)
	/** Create/retrieve a node from the given parent
		* @param parent node to search from
		* @param name of node to retrieve
		* @returns Node with given name (as path)
		*/
	protected def getUnversionedNode(parent:Node,name:String):Node = getUnversionedNode(parent,name,Dao.DefaultNodeType)
	/** Create/retrieve a node from the given parent
		* @param parent node to search from
		* @param name of node to retrieve
		* @returns Node with given name (as path)
		*/
	protected def getUnversionedNode(parent:Node,name:String,nt:String):Node =
		if (parent.hasNode(name)) {
			parent.getNode(name)
		} else {
			parent.addNode(name,nt)
		}
	/** Retrieve ContentInfo for a given node
		* @param node to get ContentInfo from
		* @returns ContentInfo populated from node
		*/
	protected def getContentInfo(node:Node) =
		new ContentInfo(
			node.getProperty(Content.DateCreated).getDate,
			node.getProperty(Content.LastModified).getDate,
			userDao.get(node.getProperty(Content.ModifiedBy).getString),
			Some(node.getUUID)
		)
	protected def saveContentInfo(node:Node,content:ContentInfo) = {
		node.setProperty(Content.DateCreated,content.dateCreated)
		node.setProperty(Content.LastModified,content.lastModified)
		node.setProperty(Content.ModifiedBy, loggedInUser.uuid.get)
	}
	// Convert a NodeIterator to an actual Iterator with generics
	implicit protected def nodeiterator2iterator(nodeIterator:NodeIterator):Iterator[Node] = new Iterator[Node] {
		def hasNext = nodeIterator.hasNext
		def next = nodeIterator.nextNode
	}
}

object Dao {
	val DefaultNodeType = "nt:unstructured"
}
