package wknyc.security

import javax.jcr.{Credentials,RepositoryException,Session,SimpleCredentials}
import org.apache.jackrabbit.core.security.authentication.Authentication
import wknyc.model.WkCredentials

class WkAuthentication(val systemSession:Session) extends Authentication {
	def canHandle(credentials:Credentials):Boolean =
		credentials.isInstanceOf[WkCredentials] || credentials.isInstanceOf[SimpleCredentials]

	@throws(classOf[RepositoryException])
	def authenticate(credentials:Credentials):Boolean = {
		/*
		if (canHandle(credentials)) {
			systemSession.getNodeByUUID(null)
			true
		} else {
			false
		}
		*/
		true
	}

}

