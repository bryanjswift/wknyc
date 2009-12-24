package wknyc.business

import org.specs.Specification
import wknyc.Config
import wknyc.model.{CaseStudy,Client,ContentInfo}

object CaseStudyManagerSpecs extends Specification with Sessioned {
	val validClient = ClientManagerSpecs.validClient
	def validCaseStudy(client:Client) = CaseStudy(ContentInfo(Config.Admin),client,"Case Study")
	val invalidCaseStudy = validators.CaseStudyValidatorSpecs.invalidCaseStudy
	"CaseStudyManager.save" should {
		"produce a Failure when not given a user" >> {
			val response = CaseStudyManager.save(validCaseStudy(validClient),None)
			response.errors.size must beGreaterThan(0)
			response.errors mustExist(_.field == "user")
		}
		"produce a Failure when given an invalid CaseStudy" >> {
			val response = CaseStudyManager.save(invalidCaseStudy,Some(Config.Admin))
			response.errors.size must beGreaterThan(0)
			response.errors mustExist(_.field == CaseStudy.Name)
		}
		"succeed when given a valid CaseStudy and User" >> {
			sessioned {
				val client = ClientManager.save(validClient,Some(Config.Admin)).payload
				val response = CaseStudyManager.save(validCaseStudy(client),Some(Config.Admin))
				response.errors.size mustBe 0
				response.payload.uuid must beSome[String]
			}
		}
	}
	"CaseStudyManager.list" should {
		"list nothing before any CaseStudys are saved" >> {
			CaseStudyManager.list.toList.size must_== 0
		}
	}
}
