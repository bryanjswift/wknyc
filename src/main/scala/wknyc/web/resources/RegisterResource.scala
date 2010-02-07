package wknyc.web.resources

import com.sun.jersey.spi.resource.Singleton
import javax.ws.rs.{POST,Produces,Path,FormParam}
import javax.ws.rs.core.{MediaType,Response}
import wknyc.Config
import wknyc.business.{Failure,Success,UserManager}
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,UserRole,WkCredentials}
import wknyc.persistence.UserDao

@Singleton
@Path("/users")
class RegisterResource {
  @POST
  @Path("/registerEmployee")
	@Produces(Array(MediaType.APPLICATION_XML))
  def registerEmployee(
		@FormParam("username") username:String,
		@FormParam("password") password:String,
		@FormParam("role") role:Long,
		@FormParam("title") title:String,
		@FormParam("firstName") firstName:String,
		@FormParam("lastName") lastName:String
  ) = {
		val user = UserManager.save(Employee(
			ContentInfo(Config.Admin),
			WkCredentials(username,password,UserRole(role),title,None),
			PersonalInfo(firstName,lastName,List[SocialNetwork]())
		),Config.Admin)
		val xml =
			user match {
				case Success(employee,message) =>
					<Response>
						<Message>Employee successfully created for {employee.username}</Message>
						<Credentials>
							<UUID>{employee.uuid.get}</UUID>
							<Username>{employee.username}</Username>
							<Role>{employee.role.display}</Role>
							<Title>{employee.title}</Title>
							<FirstName>{employee.firstName}</FirstName>
							<LastName>{employee.lastName}</LastName>
						</Credentials>
					</Response>
				case Failure(errors,message) =>
					<Response>
						<Message>Failed to save {username}</Message>
					</Response>
			}
		Response.status(Response.Status.OK).entity(xml).build
  }
}
