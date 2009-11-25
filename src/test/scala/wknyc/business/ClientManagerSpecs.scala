package wknyc.business

import org.specs.Specification
import wknyc.Config
import wknyc.model.{CaseStudy,Client,ContentInfo}

object ClientManagerSpecs extends Specification {
	val invalidClient = Client(ContentInfo(Config.Admin),"",List[CaseStudy]())
	val validClient = Client(ContentInfo(Config.Admin),"Test Client",List[CaseStudy]())
	"ClientManager.save" should {
		"produce a Failure when not given a user" >> {
			val response = ClientManager.save(validClient,None)
			response.errors.size must beGreaterThan(0)
			response.errors mustExist(_.field == "user")
		}
		"produce a Failure when given an invalid Client" >> {
			val response = ClientManager.save(invalidClient,Some(Config.Admin))
			response.errors.size must beGreaterThan(0)
			response.errors mustExist(_.field == Client.Name)
		}
		"succeed when given a valid Client and User" >> {
			val response = ClientManager.save(validClient,Some(Config.Admin))
			response.errors.size mustBe 0
			response.payload.uuid must beSome[String]
		}
		"provide a list of clients" >> {
			// open session so repository doesn't shut down during test
			val session = Config.Repository.login(Config.Admin,Config.ContentWorkspace)
			var list = ClientManager.list.toList
			list.size mustBe 0
			ClientManager.save(validClient,Some(Config.Admin))
			list = ClientManager.list.toList
			list.size mustBe 1
			// logout so repository can shut down
			session.logout
		}
	}
}
