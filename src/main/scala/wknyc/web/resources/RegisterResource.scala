package wknyc.web.resources

import com.sun.jersey.spi.resource.Singleton
import javax.ws.rs.{POST,Produces,Path,FormParam}
import javax.ws.rs.core.{MediaType,Response}
import wknyc.Config
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,WkCredentials}
import wknyc.persistence.UserDao

@Singleton
@Path("/users")
class RegisterResource {
	@POST
	@Path("/register")
	@Produces(Array(MediaType.APPLICATION_XML))
	def registerUser(
		@FormParam("username") username:String,
		@FormParam("password") password:String,
		@FormParam("department") department:String,
		@FormParam("title") title:String
	) = {
		val session = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		val dao = new UserDao(session,Config.Admin)
		try {
			val user = dao.save(WkCredentials(username,password,department,title,None))
			val xml =
				<Response>
					<Message>Credentials successfully created for {user.username}</Message>
					<Credentials>
						<UUID>{user.uuid.get}</UUID>
						<Username>{user.username}</Username>
						<Department>{user.department}</Department>
						<Title>{user.title}</Title>
					</Credentials>
				</Response>
			Response.status(Response.Status.OK).entity(xml).build
		} finally {
			dao.close
			session.logout
		}
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
		val session = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		val dao = new UserDao(session,Config.Admin)
		try {
			val employee = dao.save(Employee(
				ContentInfo(Config.Admin),
				WkCredentials(username,password,department,title,None),
				PersonalInfo(firstName,lastName,List[SocialNetwork]())
			))
			val xml =
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
			Response.status(Response.Status.OK).entity(xml).build
		} finally {
			dao.close
			session.logout
		}
  }
}
