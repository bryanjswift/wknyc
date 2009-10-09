package wknyc

import javax.jcr.Session
import org.apache.jackrabbit.api.JackrabbitNodeTypeManager
import org.apache.jackrabbit.core.TransientRepository
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl
import wknyc.model.WkCredentials

object Config {
	lazy val Repository = new TransientRepository()
	lazy val CredentialsWorkspace = "security"
	lazy val ContentWorkspace = "default"
	lazy val Admin = WkCredentials("admin@wk.com","","","",None)
	lazy val ClassLoader = getClass.getClassLoader
	private def getNodeManager(session:Session) = session.getWorkspace().getNodeTypeManager().asInstanceOf[NodeTypeManagerImpl]
	private def registerNodeTypes(filename:String,workspace:String) = {
		val definitions = ClassLoader.getResourceAsStream("wknyc/nodetype/" + filename)
		val session = Repository.login(Admin,workspace)
		val manager = getNodeManager(session)
		manager.registerNodeTypes(definitions,JackrabbitNodeTypeManager.TEXT_X_JCR_CND,true)
		session.logout
	}
	def registerSecuirtyNodeTypes = registerNodeTypes("security.cnd",CredentialsWorkspace)
	def registerDefaultNodeTypes = registerNodeTypes("default.cnd",ContentWorkspace)
}
