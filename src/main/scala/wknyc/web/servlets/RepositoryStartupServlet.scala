package wknyc.web.servlets

import javax.servlet.{GenericServlet,ServletConfig,ServletContext,ServletRequest => Request,ServletResponse => Response}
import wknyc.Config

class RepositoryStartupServlet extends GenericServlet {
	private var session = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
	override def init(config:ServletConfig) = {
		super.init(config)
	}
	override def service(request:Request,response:Response) = { }
	override def destroy() = {
		session.logout
	}
}
