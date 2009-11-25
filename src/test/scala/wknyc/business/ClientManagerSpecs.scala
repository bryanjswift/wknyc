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
	}
}
