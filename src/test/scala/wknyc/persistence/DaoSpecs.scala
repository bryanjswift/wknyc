package wknyc.persistence

import org.specs.Specification
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,WkCredentials}

object UserDaoSpecs extends Specification {
	val root = WkCredentials("root@wk.com","root","","",None)
	val emp = Employee(
			ContentInfo(root),
			WkCredentials("bryan.swift@wk.com","bs","Digital","Software Engineer",None),
			PersonalInfo("Bryan","Swift",List[SocialNetwork]())
		)
	"UserDao" should {
		"save an Employee" >> {
			val session = Config.Repository.login(WkCredentials("admin@wk.com","","","",None),"security")
			try {
				val dao = new UserDao(session,root)
				dao.save(emp) must beSome[String]
			} finally {
				session.logout
			}
		}
	}
}
