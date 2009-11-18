package wknyc.persistence

import java.util.Calendar
import org.specs.Specification
import wknyc.model.{BasicCaseStudy,CaseStudy,Client,ContentInfo,DownloadableAsset => DA,WkCredentials}

object ClientDaoSpecs extends Specification {
	"ClientDao" should {
		shareVariables()
		setSequential()
		val userDao = new UserDao(Config.Admin)
		val root = userDao.save(WkCredentials("root@wk.com","root","","",None))
		val caseStudy =
			BasicCaseStudy(
				ContentInfo(root),null,"name","Headline","Description",Calendar.getInstance,List[DA](),true,0
			)
		val cDao = new ClientDao(root)
		doAfterSpec {
			userDao.close
			cDao.close
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
