package wknyc.security

import javax.jcr.{Credentials,RepositoryException,Session}
import org.apache.jackrabbit.core.security.authentication.Authentication
import wknyc.model.WkCredentials

class WkAuthentication(val session:Session) extends Authentication {
	def canHandle(credentials:Credentials):Boolean = return credentials.isInstanceOf[WkCredentials]

	@throws(classOf[RepositoryException])
	def authenticate(credentials:Credentials):Boolean = {
		if (!canHandle(credentials)) {
			return false;
		} else {
			session.getNodeByUUID(null);
			return true;
		}
	}

}

