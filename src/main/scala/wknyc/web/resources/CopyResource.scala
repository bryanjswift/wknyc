package wknyc.web.resources

import com.sun.jersey.spi.resource.Singleton
import javax.ws.rs.{GET,Produces,Path,PathParam}
import javax.ws.rs.core.MediaType.{APPLICATION_XML,TEXT_PLAIN,TEXT_XML}

@Singleton
@Path("/copy")
class CopyResource {
	@GET
	@Path("/single/{title}")
	@Produces(Array(APPLICATION_XML,TEXT_PLAIN,TEXT_XML))
	def getCopy(@PathParam("title") title:String):String = 
		<Message>Hello Stupid.. looking for {title}</Message> toString
}

