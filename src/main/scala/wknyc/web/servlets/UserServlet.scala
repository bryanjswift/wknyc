package wknyc.web.servlets

import javax.servlet.Servlet
import velocity.{VelocityHelper,VelocityView}
import wknyc.model.User._
import wknyc.model.{ContentInfo,Employee,PersonalInfo,UserRole,WkCredentials}

trait UserServlet extends Servlet with WkServlet {
	protected def getCredentials(http:HttpHelper) =
		WkCredentials(http.parameter(Username),http.parameter(Password),UserRole(http.parameter(Role)),http.parameter(Title),None)
	protected def getPersonalInfo(http:HttpHelper) =
		PersonalInfo(http.parameter(Employee.FirstName),http.parameter(Employee.LastName),Nil)
	protected def getEmployee(http:HttpHelper) =
		Employee(ContentInfo.Empty,getCredentials(http),getPersonalInfo(http))
	protected def getUser(http:HttpHelper) = getEmployee(http)
}
