package wknyc.security

import java.security.Principal
import java.util.Map
import javax.jcr.{Credentials,RepositoryException,Session}
import javax.security.auth.callback.{Callback,CallbackHandler}
import javax.security.auth.login.LoginException
import org.apache.jackrabbit.core.security.authentication.{AbstractLoginModule,Authentication,CredentialsCallback}
import org.slf4j.{Logger,LoggerFactory}
import wknyc.model.WkCredentials

class WkLoginModule extends AbstractLoginModule {
	private val log = LoggerFactory.getLogger(classOf[WkLoginModule])

	// make sure there's no way to reset these after initialize
	private[this] var systemSession:Session = _
	private[this] var callbackHandler:CallbackHandler = _

	@throws(classOf[LoginException])
	protected def doInit(handler:CallbackHandler,systemSession:Session,options:Map[_,_]) = {
		this.systemSession = systemSession
		this.callbackHandler = handler
		log.debug("WkLoginModule.doInit finished.")
	}

	@throws(classOf[LoginException])
	@throws(classOf[RepositoryException])
	// Don't allow impersonation -- what is this for anyway?
	protected def impersonate(principal:Principal, credentials:Credentials):Boolean = false

	@throws(classOf[RepositoryException])
	protected def getAuthentication(principal:Principal, credentials:Credentials):Authentication =
		new WkAuthentication(systemSession)

	protected def getPrincipal(credentials:Credentials):Principal =
		principalProvider.getPrincipal(getUserID(credentials))

	override protected def getCredentials():Credentials = {
		val credentials = super.getCredentials
		if (credentials != null) {
			credentials
		} else {
			val callback = new CredentialsCallback()
			callbackHandler.handle(Array[Callback](callback))
			callback.getCredentials
		}
	}

	override protected def getUserID(credentials:Credentials):String =
		if (credentials.isInstanceOf[WkCredentials]) {
			credentials.asInstanceOf[WkCredentials].username
		} else {
			super.getUserID(credentials)
		}
}

