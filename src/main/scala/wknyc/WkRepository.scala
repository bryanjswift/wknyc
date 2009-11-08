package wknyc

import javax.jcr.{Credentials,Session}
import org.apache.jackrabbit.core.{SessionImpl,TransientRepository}

class WkRepository extends TransientRepository {
	private var registered = Set[String]()
	private var sessions = Set[Session]()
	override def login(credentials:Credentials,workspaceName:String) = {
		val session = super.login(credentials,workspaceName)
		sessions += session
		val workspace = session.getWorkspace.getName
		if (!registered.contains(workspace)) {
			registered += workspace
			Config.registerNodeTypes(session)
		}
		//Console.println("WkRepository::login - Num. Sessions: " + sessions.size)
		session
	}
	override def loggedOut(session:SessionImpl) = {
		sessions -= session
		if (sessions.isEmpty) {
			registered = Set[String]()
		}
		//Console.println("WkRepository::loggedOut - Num. Sessions: " + sessions.size)
		super.loggedOut(session)
	}
}
