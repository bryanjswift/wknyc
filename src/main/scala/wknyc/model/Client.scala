package wknyc.model

/** Represent the Case Study concept which is the primary display unit */
class CaseStudy(
	val contentInfo:ContentInfo,
	val assets:List[Asset],
	val description:CopyAsset,
	val downloads:List[DownloadableAsset],
	val headline:String,
	val name:String,
	val press:List[PressAsset],
	val related:List[CaseStudy],
	val studyType:String,
	val tags:List[String]
) extends Content {
	def cp(uuid:String) = new CaseStudy(contentInfo.cp(uuid),assets,description,downloads,headline,name,press,related,studyType,tags)
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
class Client(val contentInfo:ContentInfo, val name:String, val caseStudies:List[CaseStudy]) extends Content {
	def cp(uuid:String) = new Client(contentInfo.cp(uuid), name, caseStudies)
}
object Client {
	val NodeType = "wk:client"
	val Name = "name"
	val CaseStudies = "caseStudies"
}
