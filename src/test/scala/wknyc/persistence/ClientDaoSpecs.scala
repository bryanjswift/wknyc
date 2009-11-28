package wknyc.persistence

import java.util.Calendar
import org.specs.Specification
import wknyc.model.{BasicCaseStudy,CaseStudy,Client,ContentInfo,DownloadableAsset => DA,WkCredentials}
import wknyc.model.CaseStudyStatus._

object ClientDaoSpecs extends Specification {
	"ClientDao" should {
		shareVariables()
		setSequential()
		val userDao = new UserDao(Config.Admin)
		val root = userDao.save(WkCredentials("root@wk.com","root","","",None))
		val caseStudy =
			CaseStudy(ContentInfo(root),null,"name",Calendar.getInstance,"Headline","Description",List[DA](),New,0)
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
					"Test Client 3",
					List(caseStudy)
				)) // save doesn't update cascaded uuids of CaseStudy instances or members
			client.uuid must beSome[String]
			val retrieved = cDao.get(client.uuid.get)
			client.uuid must_== retrieved.uuid
		}
		"list Clients" >> {
			val all = cDao.list.toList
			all.size must beGreaterThan(0)
			all mustExist((client:Client) => client.name == "Test Client")
		}
		"update the name of an existing Client" >> {
			val client =
				cDao.save(Client(
					ContentInfo(root),
					"Test Client",
					List(caseStudy)
				))
			val updatedClient =
				cDao.save(Client(
					client.contentInfo.modifiedBy(root),
					"Updated Client",
					Nil
				))
			updatedClient.uuid must beSome[String]
			updatedClient.uuid must_== client.uuid
			val retrieved = cDao.get(updatedClient.uuid.get)
			retrieved.uuid must_== updatedClient.uuid
			retrieved.caseStudies.toList.size must_== 1
			retrieved.name must_== "Updated Client"
		}
	}
}
