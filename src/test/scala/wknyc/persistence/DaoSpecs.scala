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
			UserDao.save(emp) must beSome[String]
		}
	}
}
