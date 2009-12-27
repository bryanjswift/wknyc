package wknyc.persistence

import org.specs.Specification
import wknyc.Config
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,User,WkCredentials}

object UserDaoSpecs extends Specification {
	"UserDao" should {
		shareVariables()
		setSequential()
		val dao = new UserDao(Config.Admin)
		var root = WkCredentials("root@wk.com","root","","",None)
		doAfterSpec { dao.close }
		"save a WkCredentials" >> {
			root = dao.save(root)
			root.uuid must beSome[String] // Test credential saving
		}
		"get a WkCredentials by uuid" >> {
			val creds = dao.save(WkCredentials("bs@wk.com","bs","T","SE",None))
			creds.uuid must beSome[String]
			val retrieved = dao.get(creds.uuid.get)
			creds must_== retrieved
		}
		"get a WkCredentials by username" >> {
			val creds = dao.save(WkCredentials("bs1@wk.com","bs","T","SE",None))
			creds.uuid must beSome[String]
			val retrieved = dao.get(creds.username)
			creds must_== retrieved
		}
		"save an Employee" >> {
			val emp = Employee(
					ContentInfo(root),
					WkCredentials("bryan.swift@wk.com","bs","Digital","Software Engineer",None),
					PersonalInfo("Bryan","Swift",List[SocialNetwork]())
				)
			dao.save(emp).uuid must beSome[String] // Test employee saving
		}
		"get an Employee by uuid" >> {
			var emp = Employee(
					ContentInfo(root),
					WkCredentials("bryan.swift1@wk.com","bs","Digital","Software Engineer",None),
					PersonalInfo("Bryan","Swift",SocialNetwork("Twitter","http://twitter.com/bryanjswift") :: Nil)
				)
			emp = dao.save(emp)
			emp.uuid must beSome[String]
			val retrieved = dao.get(emp.uuid.get)
			emp must_== retrieved
		}
		"get an Employee by username" >> {
			var emp = Employee(
					ContentInfo(root),
					WkCredentials("bryan.swift2@wk.com","bs","Digital","Software Engineer",None),
					PersonalInfo("Bryan","Swift",SocialNetwork("Twitter","http://twitter.com/bryanjswift") :: Nil)
				)
			emp = dao.save(emp)
			emp.uuid must beSome[String]
			val retrieved = dao.get(emp.username)
			emp must_== retrieved
		}
		"list all saved Users" >> {
			val users = dao.list
			// depends on tests above it
			users.toList.size must_== 6
			users.exists(_.username == "root@wk.com")
			users.exists(_.username == "bs@wk.com")
			users.exists(_.username == "bs1@wk.com")
			users.exists(_.username == "bryan.swift@wk.com")
			users.exists(_.username == "bryan.swift1@wk.com")
			users.exists(_.username == "bryan.swift2@wk.com")
		}
	}
}
