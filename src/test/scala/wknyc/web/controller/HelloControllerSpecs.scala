package wknyc.web.controller

import org.specs.Specification

object HelloControllerSpecs extends Specification {
	"HelloController" should {
		"provide a hello.jsp view" >> {
			val controller = new HelloController
			val mv = controller.handleRequest(null,null)
			mv.getViewName must_== "hello.jsp"
		}
	}
}
