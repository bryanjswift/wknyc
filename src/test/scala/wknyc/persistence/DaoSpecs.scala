package wknyc.persistence

import org.specs.Specification
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,User,WkCredentials}

object UserDaoSpecs extends Specification {
	"UserDao" should {
		shareVariables()
		val session = Config.Repository.login(Config.Admin,"security")
		var root = WkCredentials("root@wk.com","root","","",None)
		doAfterSpec { session.logout }
		"save an Employee" >> {
			var dao = new UserDao(session,root)
			root = dao.save(root)
			root.uuid must beSome[String]
			dao = new UserDao(session,root)
			val emp = Employee(
					ContentInfo(root),
					WkCredentials("bryan.swift@wk.com","bs","Digital","Software Engineer",None),
					PersonalInfo("Bryan","Swift",List[SocialNetwork]())
				)
			dao.save(emp).uuid must beSome[String]
		}
		"get an Employee by uuid" >> {
			val dao = new UserDao(session,root)
			var emp = Employee(
					ContentInfo(root),
					WkCredentials("bryan.swift1@wk.com","bs","Digital","Software Engineer",None),
					PersonalInfo("Bryan","Swift",List[SocialNetwork]())
				)
			emp = dao.save(emp)
			emp.uuid must beSome[String]
			val retrieved = dao.get(emp.uuid.get)
			emp must_== retrieved
		}
		"get an Employee by username" >> {
			val dao = new UserDao(session,root)
			var emp = Employee(
					ContentInfo(root),
					WkCredentials("bryan.swift2@wk.com","bs","Digital","Software Engineer",None),
					PersonalInfo("Bryan","Swift",List[SocialNetwork]())
				)
			emp = dao.save(emp)
			emp.uuid must beSome[String]
			val retrieved = dao.get(emp.username)
			emp must_== retrieved
		}
	}
}
