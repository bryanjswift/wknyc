package wknyc.business

import org.specs.Specification
import wknyc.Config
import wknyc.model.{CaseStudy,Client,ContentInfo}

object ClientManagerSpecs extends Specification with Sessioned {
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
	"ClientManager.list" should {
		"provide a list of clients" >> {
			sessioned {
				var list = ClientManager.list.toList
				list.size mustBe 0
				ClientManager.save(validClient,Some(Config.Admin))
				list = ClientManager.list.toList
				list.size mustBe 1
			}
		}
	}
	"ClientManager.get" should {
		"retrieve a Client by uuid if uuid exists" >> {
			sessioned {
				val saved = ClientManager.save(validClient,Some(Config.Admin)).payload
				val retrieved = ClientManager.get(saved.uuid.get)
				saved.name must_== retrieved.name
				saved.uuid must_== retrieved.uuid
				// testing this way because the collections are different types
				saved.caseStudies must haveTheSameElementsAs(retrieved.caseStudies)
			}
		}
	}
	"ClientManager.getByName" should {
		"retrieve a Some(Client) by name existing" >> {
			sessioned {
				val saved = ClientManager.save(validClient,Some(Config.Admin)).payload
				val retrieved = ClientManager.getByName(validClient.name)
				retrieved must beSome[Client]
				saved must_== retrieved.get
			}
		}
		"retrieve a None if no Client by name" >> {
			val retrieved = ClientManager.getByName("Tester")
			retrieved must beNone
		}
	}
}
