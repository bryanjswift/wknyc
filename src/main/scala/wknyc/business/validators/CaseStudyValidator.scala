package wknyc.business.validators

import wknyc.model.CaseStudy
import wknyc.model.CaseStudy._

object CaseStudyValidator extends Validator {
	def validate(target:AnyRef) =
		target match {
			case study:CaseStudy => validateCaseStudy(study)
			case _ => throw new IllegalArgumentException(String.format("%s is not a known CaseStudy type",target.getClass.getName))
		}
	// Type Validation
	private def validateCaseStudy(study:CaseStudy) = ( // this is one statement
		validateName(study.name)
		:: validateHeadline(study.headline)
		:: validateDescription(study.description)
		:: Nil
	)
	// Field Validation
	private def validateName(name:String) = required(name,Name)
	private def validateHeadline(headline:String) = required(headline,Headline)
	private def validateDescription(description:String) = required(description,Description)
}
