package wknyc.business

import org.specs.Specification
import wknyc.Config
import wknyc.model.{CaseStudy,Client,ContentInfo}

object ClientManagerSpecs extends Specification {
	val invalidClient = Client(ContentInfo(Config.Admin),"",List[CaseStudy]())
	val validClient = Client(ContentInfo(Config.Admin),"Test Client",List[CaseStudy]())
	"ClientManager.save" should {
		"produce a Failure when not given a user" >> {
			ClientManager.save(validClient,None) match {
				case Failure(errors,message) =>
					errors.size must beGreaterThan(0)
				case Success(payload,message) => fail("Failure result should be produced for non-existant user")
			}
		}
		"produce a Failure when given an invalid Client" >> {
			ClientManager.save(invalidClient,Some(Config.Admin)) match {
				case Failure(errors,message) =>
					errors.size must beGreaterThan(0)
				case Success(payload,message) => fail("Failure result should be produced for invalid client")
			}
		}
	}
}
