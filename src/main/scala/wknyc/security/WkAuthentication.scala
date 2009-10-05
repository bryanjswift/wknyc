package wknyc.security

import javax.jcr.{Credentials,RepositoryException,Session}
import org.apache.jackrabbit.core.security.authentication.Authentication
import wknyc.model.WkCredentials

class WkAuthentication(val session:Session) extends Authentication {
	def canHandle(credentials:Credentials):Boolean = credentials.isInstanceOf[WkCredentials]

	@throws(classOf[RepositoryException])
	def authenticate(credentials:Credentials):Boolean = {
		/*
		if (!canHandle(credentials)) {
			false;
		} else {
			session.getNodeByUUID(null);
			true;
		}
		*/
		true
	}

}

