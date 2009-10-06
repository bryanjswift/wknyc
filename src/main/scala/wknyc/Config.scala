package wknyc

import org.apache.jackrabbit.api.JackrabbitNodeTypeManager
import org.apache.jackrabbit.core.TransientRepository
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl
import wknyc.model.WkCredentials

object Config {
	lazy val Repository = new TransientRepository()
	def registerNodeTypes = {
		val nodeTypeDefinition = getClass().getClassLoader().getResourceAsStream("wknyc/nodetype/types.cnd")
		val session = Config.Repository.login(WkCredentials("admin@wk.com","","","",None))
		val manager = session.getWorkspace().getNodeTypeManager().asInstanceOf[NodeTypeManagerImpl]
		manager.registerNodeTypes(nodeTypeDefinition,JackrabbitNodeTypeManager.TEXT_X_JCR_CND)
		session.logout
	}
}
