package wknyc.persistence

import org.specs.Specification
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,User,WkCredentials}

object UserDaoSpecs extends Specification {
	var root:User = WkCredentials("root@wk.com","root","","",None)
	"UserDao" should {
		"save an Employee" >> {
			val session = Config.Repository.login(WkCredentials("admin@wk.com","","","",None),"security")
			try {
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
			} finally {
				session.logout
			}
		}
	}
}
