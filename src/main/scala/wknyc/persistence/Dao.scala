package wknyc.persistence

import javax.jcr.{Node,NodeIterator,Session}
import wknyc.model.User

class Dao(private val session:Session, private val loggedInUser:User) {
	protected lazy val root = session.getRootNode
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
	// Convert a NodeIterator to an actual Iterator with generics
	implicit protected def nodeiterator2iterator(nodeIterator:NodeIterator):Iterator[Node] = new Iterator[Node] {
		def hasNext = nodeIterator.hasNext
		def next = nodeIterator.nextNode
	}
}

object Dao {
	val DefaultNodeType = "nt:unstructured"
}
