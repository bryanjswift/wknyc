package wknyc.security

import javax.jcr.{Credentials,RepositoryException,Session,SimpleCredentials}
import org.apache.jackrabbit.core.security.authentication.Authentication
import wknyc.model.WkCredentials

class WkAuthentication(private val systemSession:Session) extends Authentication {
	def canHandle(credentials:Credentials):Boolean =
		credentials.isInstanceOf[WkCredentials]

	@throws(classOf[RepositoryException])
	def authenticate(credentials:Credentials):Boolean = {
		if (canHandle(credentials)) {
			val creds = credentials.asInstanceOf[WkCredentials]
			try {
				val credsNode = systemSession.getNodeByUUID(creds.uuid.get)
				val password = credsNode.getProperty("password")
				password == creds.password
				true
			} catch {
				case e:Exception =>
					// user doesn't exist yet
					true
			}
		} else {
			false
		}
	}

}

