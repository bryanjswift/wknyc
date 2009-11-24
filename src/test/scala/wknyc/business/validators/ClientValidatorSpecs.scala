package wknyc.business.validators

import org.specs.Specification
import wknyc.model.{CaseStudy,Client,ContentInfo,SocialNetwork}

object ClientValidatorSpecs extends Specification {
	"ClientValidator.validate" should {
		"throw IllegalArgumentException if not validating Client" >> {
			ClientValidator.validate(SocialNetwork("Twitter","http://twitter.com/bjs")) must throwA[IllegalArgumentException]
		}
		"throw IllegalArgumentException if validating null" >> {
			ClientValidator.validate(null) must throwA[IllegalArgumentException]
		}
		"return zero errors if Client.name is not null and not empty" >> {
			val results = ClientValidator.validate(Client(ContentInfo(Config.Admin),"T",List[CaseStudy]()))
			results mustContain(ValidationSuccess(Client.Name))
		}
		"return an error for name if Client.name is empty" >> {
			val results = ClientValidator.validate(Client(ContentInfo(Config.Admin),"",List[CaseStudy]()))
			results mustContain(ValidationError(Client.Name,String.format("%s can not be empty.",Client.Name)))
		}
	}
}