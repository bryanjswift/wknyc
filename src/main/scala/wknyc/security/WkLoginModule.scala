package wknyc.security

import java.security.Principal
import java.util.Map
import javax.jcr.{Credentials,RepositoryException,Session}
import javax.security.auth.callback.CallbackHandler
import javax.security.auth.login.LoginException
import org.apache.jackrabbit.core.security.authentication.{AbstractLoginModule,Authentication}
import org.slf4j.{Logger,LoggerFactory}
import wknyc.model.WkCredentials

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
		return new WkAuthentication(session)

	protected def getPrincipal(credentials:Credentials):Principal =
		principalProvider.getPrincipal(getUserID(credentials));

	override protected def getUserID(credentials:Credentials):String =
		if (credentials.isInstanceOf[WkCredentials]) {
Console.println("is WkCredentials")
			credentials.asInstanceOf[WkCredentials].username
		} else {
Console.println("using anonymous")
			// anonymousId defined in AbstractLoginModule
			anonymousId
		}
}

