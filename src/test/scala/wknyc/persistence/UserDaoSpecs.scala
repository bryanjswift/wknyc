package wknyc.persistence

import org.specs.Specification
import wknyc.Config
import wknyc.model.{ContentInfo,Employee,PersonalInfo,SocialNetwork,User,WkCredentials}

object UserDaoSpecs extends Specification {
	"UserDao" should {
		shareVariables()
		setSequential()
		val session = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		var root = WkCredentials("root@wk.com","root","","",None)
		doAfterSpec { session.logout }
		"save a WkCredentials" >> {
			var dao = new UserDao(session,root)
			root = dao.save(root)
			root.uuid must beSome[String] // Test credential saving
		}
		"get a WkCredentials by uuid" >> {
			val dao = new UserDao(session,root)
			val creds = dao.save(WkCredentials("bs@wk.com","bs","T","SE",None))
			creds.uuid must beSome[String]
			val retrieved = dao.get(creds.uuid.get)
			creds must_== retrieved
		}
		"get a WkCredentials by username" >> {
			val dao = new UserDao(session,root)
			val creds = dao.save(WkCredentials("bs1@wk.com","bs","T","SE",None))
			creds.uuid must beSome[String]
			val retrieved = dao.get(creds.username)
			creds must_== retrieved
		}
		"save an Employee" >> {
			val dao = new UserDao(session,root)
			val emp = Employee(
					ContentInfo(root),
					WkCredentials("bryan.swift@wk.com","bs","Digital","Software Engineer",None),
					PersonalInfo("Bryan","Swift",List[SocialNetwork]())
				)
			dao.save(emp).uuid must beSome[String] // Test employee saving
		}
		"get an Employee by uuid" >> {
			val dao = new UserDao(session,root)
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
			val dao = new UserDao(session,root)
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
	}
}
