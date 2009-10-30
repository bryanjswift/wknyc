package wknyc.web.resources

import com.sun.jersey.spi.resource.Singleton
import javax.ws.rs.{POST,Produces,Path,FormParam}
import javax.ws.rs.core.{MediaType,Response}
import wknyc.Config
import wknyc.business.UserManager
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,WkCredentials}
import wknyc.persistence.UserDao

@Singleton
@Path("/users")
class RegisterResource {
	@POST
	@Path("/registerCredentials")
	@Produces(Array(MediaType.APPLICATION_XML))
	def registerUser(
		@FormParam("username") username:String,
		@FormParam("password") password:String,
		@FormParam("department") department:String,
		@FormParam("title") title:String
	) = {
		val user = UserManager.save(WkCredentials(username,password,department,title,None),Config.Admin)
		val xml =
			if (user.isEmpty) {
				<Response>
					<Message>Failed to save {username}</Message>
				</Response>
			} else {
				val creds = user.get
				<Response>
					<Message>Credentials successfully created for {creds.username}</Message>
					<Credentials>
						<UUID>{creds.uuid.get}</UUID>
						<Username>{creds.username}</Username>
						<Department>{creds.department}</Department>
						<Title>{creds.title}</Title>
					</Credentials>
				</Response>
			}
		Response.status(Response.Status.OK).entity(xml).build
	}
  @POST
  @Path("/registerEmployee")
	@Produces(Array(MediaType.APPLICATION_XML))
  def registerEmployee(
		@FormParam("username") username:String,
		@FormParam("password") password:String,
		@FormParam("department") department:String,
		@FormParam("title") title:String,
		@FormParam("firstName") firstName:String,
		@FormParam("lastName") lastName:String
  ) = {
		val user = UserManager.save(Employee(
			ContentInfo(Config.Admin),
			WkCredentials(username,password,department,title,None),
			PersonalInfo(firstName,lastName,List[SocialNetwork]())
		),Config.Admin)
		val xml =
			if (user.isEmpty) {
				<Response>
					<Message>Failed to save {username}</Message>
				</Response>
			} else {
				val employee = user.get
				<Response>
					<Message>Employee successfully created for {employee.username}</Message>
					<Credentials>
						<UUID>{employee.uuid.get}</UUID>
						<Username>{employee.username}</Username>
						<Department>{employee.department}</Department>
						<Title>{employee.title}</Title>
						<FirstName>{employee.firstName}</FirstName>
						<LastName>{employee.lastName}</LastName>
					</Credentials>
				</Response>
			}
		Response.status(Response.Status.OK).entity(xml).build
  }
}
