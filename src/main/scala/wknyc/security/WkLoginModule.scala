package wknyc.security

import java.security.Principal
import java.util.Map
import javax.jcr.{Credentials,RepositoryException,Session}
import javax.security.auth.callback.CallbackHandler
import javax.security.auth.login.LoginException
import org.apache.jackrabbit.core.security.authentication.{AbstractLoginModule,Authentication}
import org.slf4j.{Logger,LoggerFactory}

class WkLoginModule extends AbstractLoginModule {
	private val log = LoggerFactory.getLogger(classOf[WkLoginModule]);

	var session:Session = _

	@throws(classOf[LoginException])
	protected def doInit(handler:CallbackHandler,session:Session,options:Map[_,_]) = {
		this.session = session;
		log.debug("WkLoginModule.doInit finished.");
	}
	@throws(classOf[LoginException])
	@throws(classOf[RepositoryException])
	protected def impersonate(principal:Principal, credentials:Credentials):Boolean = {
		// Don't allow impersonation
		return false;
	}
	@throws(classOf[RepositoryException])
	protected def getAuthentication(principal:Principal, credentials:Credentials):Authentication =
		throw new UnsupportedOperationException("Not supported yet.");
	protected def getPrincipal(credentials:Credentials):Principal =
		throw new UnsupportedOperationException("Not supported yet.");
}

