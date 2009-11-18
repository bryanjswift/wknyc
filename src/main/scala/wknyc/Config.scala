package wknyc

import javax.jcr.Session
import javax.jcr.nodetype.NodeType
import org.apache.jackrabbit.api.JackrabbitNodeTypeManager
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl
import wknyc.model.WkCredentials

object Config {
	lazy val Repository = new WkRepository()
	lazy val CredentialsWorkspace = "security"
	lazy val ContentWorkspace = "content"
	lazy val Admin = WkCredentials("repositoryAdmin@wk.com","g0du5er","Admin","Admin",Some("Config.Admin.UUID"))
	lazy val ClassLoader = getClass.getClassLoader
	private val systemProps = List("org.apache.jackrabbit.repository.home","org.apache.jackrabbit.repository.conf")
	systemProps.foreach(key => System.setProperty(key,Props(key)))
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
