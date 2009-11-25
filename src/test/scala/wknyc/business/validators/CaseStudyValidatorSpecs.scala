package wknyc.business.validators

import java.util.Calendar
import org.specs.Specification
import wknyc.model.{BasicCaseStudy,CaseStudy,Client,ContentInfo}

object CaseStudyValidatorSpecs extends Specification {
	val client = Client(ContentInfo(Config.Admin),"T",List[CaseStudy]())
	"CaseStudyValidator.validate" should {
		"throw IllegalArgumentException if not validating CaseStudy" >> {
			CaseStudyValidator.validate(client) must throwA[IllegalArgumentException]
		}
		"throw IllegalArgumentException if validating null" >> {
			CaseStudyValidator.validate(null) must throwA[IllegalArgumentException]
		}
		"return Failure if name invalid" >> {
			val results =
				CaseStudyValidator.validate(
					BasicCaseStudy(
						ContentInfo(Config.Admin),
						client,
						"Case Study",
						"Headline",
						"Description",
						Calendar.getInstance,
						Nil,
						false,
						1
					)
				)
			results mustContain(ValidationSuccess(CaseStudy.Name))
		}
	}
}
