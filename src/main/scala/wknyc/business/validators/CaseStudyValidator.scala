package wknyc.business.validators

import wknyc.model.{CaseStudy,Client,DownloadableAsset => Download,ImageAsset}
import wknyc.model.CaseStudy.{Client => _, _} // Import all constants but Client

object CaseStudyValidator extends Validator {
	def validate(target:AnyRef) =
		target match {
			case study:CaseStudy => validateCaseStudy(study)
			case null => throw new IllegalArgumentException("CaseStudy may not be null")
			case _ => throw new IllegalArgumentException(String.format("%s is not a known CaseStudy type",target.getClass.getName))
		}
	// Type Validation
	private def validateCaseStudy(study:CaseStudy) = ( // this is one statement
		validateName(study.name)
		:: validateClient(study.client)
		:: Nil
	)
	// Field Validation
	private def validateName(name:String) = required(name,Name)
	private def validateClient(client:Client) =
		client match {
			case null => ValidationError(CaseStudy.Client,String.format("%s is required",CaseStudy.Client))
			case Client(info,name,studies) => required(name,CaseStudy.Client)
		}
	private def validateHeadline(headline:String) = required(headline,Headline)
	private def validateDescription(description:String) = required(description,Description)
	private def validateVideo(video:Download) =
		video match {
			case null => ValidationError(Video,String.format("%s is required when Art Complete",Video))
		}
	private def validateImages(images:Iterable[ImageAsset]) = ValidationSuccess(Images)
	// Validate by status
	private def validateCopyComplete(study:CaseStudy) = ( // this is one statement
		validateHeadline(study.headline)
		:: validateDescription(study.description)
		:: Nil
	)
	private def validateArtComplete(study:CaseStudy) = ( // this is one statement
		validateVideo(study.video)
		:: validateImages(study.images)
		:: Nil
	)
	private def validateComplete(study:CaseStudy) = ( // this is one statement
		validateArtComplete(study)
		::: validateCopyComplete(study)
	)
}
