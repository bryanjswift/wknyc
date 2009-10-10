package wknyc

import javax.jcr.{Credentials,Session}
import org.apache.jackrabbit.api.JackrabbitNodeTypeManager
import org.apache.jackrabbit.core.TransientRepository
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl
import wknyc.model.WkCredentials

object Config {
	lazy val Repository = new WkRepository()
	lazy val CredentialsWorkspace = "security"
	lazy val ContentWorkspace = "default"
	lazy val Admin = WkCredentials("admin@wk.com","","","",None)
	lazy val ClassLoader = getClass.getClassLoader
	private def registerNodeTypes(filename:String,session:Session):Unit = {
		val definitions = ClassLoader.getResourceAsStream("wknyc/nodetype/" + filename)
		val manager = session.getWorkspace().getNodeTypeManager().asInstanceOf[NodeTypeManagerImpl]
		manager.registerNodeTypes(definitions,JackrabbitNodeTypeManager.TEXT_X_JCR_CND,true)
	}
	def registerSecurityNodeTypes(session:Session):Unit = registerNodeTypes("security.cnd",session)
	def registerDefaultNodeTypes(session:Session):Unit = registerNodeTypes("default.cnd",session)
}

class WkRepository extends TransientRepository {
	var registered = Map[String,Boolean]()
	override def login(credentials:Credentials,workspaceName:String) = {
		val session = super.login(credentials,workspaceName)
		val workspace = session.getWorkspace.getName
		if (!registered.getOrElse(workspace,false)) {
			registered += (workspace -> true)
			workspace match {
				case Config.CredentialsWorkspace => Config.registerSecurityNodeTypes(session)
				case Config.ContentWorkspace => Config.registerDefaultNodeTypes(session)
				case _ => ()
			}
		}
		session
	}
}
