package wknyc.web.servlets

import javax.servlet.{GenericServlet,ServletConfig,ServletContext,ServletRequest => Request,ServletResponse => Response}
import org.apache.commons.logging.LogFactory
import wknyc.Config

class RepositoryStartupServlet extends GenericServlet {
	private lazy val log = LogFactory.getLog(getClass)
	private val session = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
	override def init(config:ServletConfig) = {
		log.info("Repository session opened")
		super.init(config)
	}
	override def service(request:Request,response:Response) = { }
	override def destroy() = {
		log.info("Closing Repository session")
		session.logout
	}
}
