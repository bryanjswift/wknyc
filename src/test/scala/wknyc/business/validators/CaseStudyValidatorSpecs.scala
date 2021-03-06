package wknyc.business.validators

import java.util.Calendar
import org.specs.Specification
import wknyc.model.{BasicCaseStudy,CaseStudy,Client,ContentInfo}

object CaseStudyValidatorSpecs extends Specification {
	val client = Client(ContentInfo(Config.Admin),"T",Nil)
	val invalidCaseStudy = CaseStudy(ContentInfo(Config.Admin),client,"")
	val validCaseStudy = CaseStudy(ContentInfo(Config.Admin),client,"Case Study")
	"CaseStudyValidator.validate" should {
		"throw IllegalArgumentException if not validating CaseStudy" >> {
			CaseStudyValidator.validate(client) must throwA[IllegalArgumentException]
		}
		"throw IllegalArgumentException if validating null" >> {
			CaseStudyValidator.validate(null) must throwA[IllegalArgumentException]
		}
		"return Success if name valid" >> {
			val results = CaseStudyValidator.validate(validCaseStudy)
			results mustContain(ValidationSuccess(CaseStudy.Name))
			results mustContain(ValidationSuccess(CaseStudy.Client))
		}
	}
}
