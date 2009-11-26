package wknyc.business.validators

import java.util.Calendar
import wknyc.model.{CaseStudy,Client,DownloadableAsset => Download,ImageAsset}

object CaseStudyValidator extends Validator {
	def validate(target:AnyRef) =
		target match {
			case study:CaseStudy => validateCaseStudy(study)
			case null => throw new IllegalArgumentException("CaseStudy may not be null")
			case _ => throw new IllegalArgumentException(String.format("%s is not a known CaseStudy type",target.getClass.getName))
		}
	// Type Validation
	private def validateCaseStudy(study:CaseStudy) =
		study.status match {
			case _ => validateNew(study)
		}
	// Field Validation
	private def validateName(name:String) = required(name,CaseStudy.Name)
	private def validateClient(client:Client) =
		client match {
			case null => ValidationError(CaseStudy.Client,String.format("%s is required",CaseStudy.Client))
			case Client(info,name,studies) => required(name,CaseStudy.Client)
		}
	private def validateHeadline(headline:String) = required(headline,CaseStudy.Headline)
	private def validateDescription(description:String) = required(description,CaseStudy.Description)
	private def validateVideo(video:Download) =
		video match {
			case null => ValidationError(CaseStudy.Video,String.format("%s is required when Art Complete",CaseStudy.Video))
			case _ => ValidationSuccess(CaseStudy.Video)
		}
	private def validateImages(images:Iterable[ImageAsset]) = 
		if (images.elements.hasNext) ValidationSuccess(CaseStudy.Images)
		else ValidationError(CaseStudy.Images,"There must be at least one image when Art Complete")
	private def validateLaunch(launch:Calendar) =
		launch match {
			case null => ValidationError(CaseStudy.Launch,String.format("%s is required",CaseStudy.Launch))
			case _ => ValidationSuccess(CaseStudy.Launch)
		}
	// Validate by status
	private def validateNew(study:CaseStudy) =
		(validateName(study.name)
		:: validateClient(study.client)
		:: Nil)
	private def validateNormal(study:CaseStudy) = validateNew(study)
	private def validateCopyComplete(study:CaseStudy) =
		(validateHeadline(study.headline)
		:: validateDescription(study.description)
		:: Nil)
	private def validateArtComplete(study:CaseStudy) =
		(validateVideo(study.video)
		:: validateImages(study.images)
		:: Nil)
	private def validateComplete(study:CaseStudy) =
		(validateArtComplete(study)
		::: validateCopyComplete(study))
	private def validatePublished(study:CaseStudy) =
		(validateLaunch(study.launch)
		:: Nil
		::: validateArtComplete(study)
		::: validateCopyComplete(study))
}
