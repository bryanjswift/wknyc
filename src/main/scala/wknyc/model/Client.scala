package wknyc.model

/** Represent the Case Study concept which is the primary display unit */
case class CaseStudy(
	contentInfo:ContentInfo,
	assets:List[Asset],
	description:CopyAsset,
	downloads:List[DownloadableAsset],
	headline:String,
	name:String,
	press:List[PressAsset],
	related:List[CaseStudy],
	studyType:String,
	tags:List[String]
) extends Content {
	def cp(uuid:String) = CaseStudy(contentInfo.cp(uuid),assets,description,downloads,headline,name,press,related,studyType,tags)
}

object CaseStudy {
	val NodeType = "wk:caseStudy"
	val Assets = "assets"
	val Description = "description"
	val Downloads = "downloads"
	val Headline = "headline"
	val Name = "name"
	val Press = "press"
	val Related = "related"
	val StudyType = "studyType"
	val Tags = "tags"
}
/** Represent a collection of CaseStudy objects */
case class Client(contentInfo:ContentInfo, name:String, caseStudies:List[CaseStudy]) extends Content {
	def cp(uuid:String) = Client(contentInfo.cp(uuid), name, caseStudies)
}
object Client {
	val NodeType = "wk:client"
	val Name = "name"
	val CaseStudies = "caseStudies"
}
