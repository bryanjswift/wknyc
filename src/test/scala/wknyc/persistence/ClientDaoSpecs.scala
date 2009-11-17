package wknyc.persistence

import java.util.Calendar
import org.specs.Specification
import wknyc.model.{BasicCaseStudy,CaseStudy,Client,ContentInfo,DownloadableAsset,WkCredentials}

object ClientDaoSpecs extends Specification {
	"ClientDao" should {
		shareVariables()
		setSequential()
		val securitySession = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		val session = Config.Repository.login(Config.Admin,Config.ContentWorkspace)
		val userDao = new UserDao(securitySession,Config.Admin)
		val root = userDao.save(WkCredentials("root@wk.com","root","","",None))
		val caseStudy =
			BasicCaseStudy(
				ContentInfo(root),
				"name",
				"Headline",
				"Description",
				Calendar.getInstance,
				List[DownloadableAsset](),
				true,
				0
			)
		val cDao = new ClientDao(session,root)
		doAfterSpec {
			cDao.close
			securitySession.logout
			session.logout
		}
		"save a Client without CaseStudy instances" >> {
			val client =
				cDao.save(Client(
					ContentInfo(root),
					"Test Client",
					List[CaseStudy]()
				))
			client.uuid must beSome[String]
		}
		"save a Client with CaseStudy instances" >> {
			val client =
				cDao.save(Client(
					ContentInfo(root),
					"Test Client",
					List(caseStudy)
				))
			client.uuid must beSome[String]
		}
		"get a Client" >> {
			val client =
				cDao.save(Client(
					ContentInfo(root),
					"Test Client",
					List(caseStudy)
				)) // save doesn't update cascaded uuids of CaseStudy instances or members
			client.uuid must beSome[String]
			val retrieved = cDao.get(client.uuid.get)
			client.uuid must_== retrieved.uuid
		}
	}
}
