package wknyc.security

import javax.jcr.{Credentials,Node,RepositoryException,Session,SimpleCredentials}
import org.apache.jackrabbit.core.security.authentication.Authentication
import wknyc.Config
import wknyc.model.WkCredentials

class WkAuthentication(private val systemSession:Session) extends Authentication {
	def canHandle(credentials:Credentials):Boolean =
		credentials.isInstanceOf[WkCredentials]

	@throws(classOf[RepositoryException])
	def authenticate(credentials:Credentials):Boolean = {
		if (canHandle(credentials)) {
			val creds = credentials.asInstanceOf[WkCredentials]
			creds.uuid match {
				case Some(uuid) =>
					try {
						val credsNode = systemSession.getNodeByUUID(uuid)
						val password = credsNode.getProperty("password").getString
						val username = credsNode.getProperty("username").getString
						username == creds.username && password == creds.password
					} catch {
						case e:Exception =>
							// user doesn't exist yet
							false
					}
				case None => creds.eq(Config.Admin)
			}
		} else {
			false
		}
	}
}

object WkAuthentication {
	def nodeToCreds(n:Node) =
		WkCredentials(
			n.getProperty("username").getString,
			n.getProperty("password").getString,
			n.getProperty("department").getString,
			n.getProperty("title").getString,
			Some(n.getUUID)
		)
}
