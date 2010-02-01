package wknyc.security

import javax.jcr.{Credentials,Node,RepositoryException,Session,SimpleCredentials}
import org.apache.jackrabbit.core.security.authentication.Authentication
import wknyc.Config
import wknyc.model.User

class WkAuthentication(private val systemSession:Session) extends Authentication {
	def canHandle(credentials:Credentials):Boolean =
		credentials.isInstanceOf[User]

	@throws(classOf[RepositoryException])
	def authenticate(credentials:Credentials):Boolean = {
		if (canHandle(credentials)) {
			val creds = credentials.asInstanceOf[User]
			creds.uuid match {
				case Config.Admin.uuid => creds.eq(Config.Admin)
				case Some(uuid) =>
					try {
						val credsNode = systemSession.getNodeByUUID(uuid)
						val password = credsNode.getProperty("password").getString
						val username = credsNode.getProperty("username").getString
						val active = credsNode.getProperty("active").getBoolean
						// Don't use encryption here because credentials already has encrypted password
						username == creds.username && password == creds.password && active
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

