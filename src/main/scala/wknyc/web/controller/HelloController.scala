package wknyc.web.controller

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.{HttpServletRequest => Request,HttpServletResponse => Response}
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.AbstractUrlViewController
import org.apache.commons.logging.{Log,LogFactory}

class HelloController extends AbstractUrlViewController {
	protected def getViewNameForRequest(request:Request) = "hello.jsp"
	@throws(classOf[ServletException])
	@throws(classOf[IOException])
	override def handleRequestInternal(request:Request, response:Response) = {
		logger.info("Returning hello view")
		new ModelAndView(getViewNameForRequest(request))
	}
}

