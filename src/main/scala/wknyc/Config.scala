package wknyc

import javax.jcr.{Credentials,Session}
import javax.jcr.nodetype.NodeType
import org.apache.jackrabbit.api.JackrabbitNodeTypeManager
import org.apache.jackrabbit.core.{SessionImpl,TransientRepository}
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl
import wknyc.model.WkCredentials

object Config {
	lazy val Repository = new WkRepository()
	lazy val CredentialsWorkspace = "security"
	lazy val ContentWorkspace = "content"
	lazy val Admin = WkCredentials("repositoryAdmin@wk.com","Jus1 4 dummy pa5sw0r6","Administration","Administrator",None)
	lazy val ClassLoader = getClass.getClassLoader
	def registerNodeTypes(session:Session):Array[NodeType] =
		session.getWorkspace.getName match {
			case CredentialsWorkspace => registerNodeTypes("security.cnd",session)
			case ContentWorkspace => registerNodeTypes("default.cnd",session)
			case _ => Array[NodeType]()
		}
	private def registerNodeTypes(filename:String,session:Session):Array[NodeType] = {
		val definitions = ClassLoader.getResourceAsStream("wknyc/nodetype/" + filename)
		val manager = session.getWorkspace().getNodeTypeManager().asInstanceOf[NodeTypeManagerImpl]
		manager.registerNodeTypes(definitions,JackrabbitNodeTypeManager.TEXT_X_JCR_CND,true)
	}
}

class WkRepository extends TransientRepository {
	private var registered = Map[String,Boolean]()
	private var sessions = Set[Session]()
	override def login(credentials:Credentials,workspaceName:String) = {
		val session = super.login(credentials,workspaceName)
		sessions += session
		val workspace = session.getWorkspace.getName
		if (!registered.getOrElse(workspace,false)) {
			registered += (workspace -> true)
			Config.registerNodeTypes(session)
		}
		//Console.println("WkRepository::login - Num. Sessions: " + sessions.size)
		session
	}
	override def loggedOut(session:SessionImpl) = {
		sessions -= session
		if (sessions.isEmpty) {
			registered = Map[String,Boolean]()
		}
		//Console.println("WkRepository::loggedOut - Num. Sessions: " + sessions.size)
		super.loggedOut(session)
	}
}
