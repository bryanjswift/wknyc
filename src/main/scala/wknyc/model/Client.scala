package wknyc.model

/** Represent the Case Study concept which is the primary display unit */
case class CaseStudy(
	contentInfo:ContentInfo,
	name:String,
	headline:String,
	studyType:String,
	tags:List[String],
	related:List[CaseStudy],
	video:DownloadableAsset,
	description:CopyAsset,
	images:Iterable[ImageAsset],
	downloads:Iterable[DownloadableAsset],
	press:Iterable[PressAsset]
) extends Content {
	def cp(uuid:String) = CaseStudy(contentInfo.cp(uuid),name,headline,studyType,tags,related,video,description,images,downloads,press)
}

object CaseStudy {
	val NodeType = "wk:caseStudy"
	val Description = "description"
	val Downloads = "downloads"
	val Headline = "headline"
	val Images = "images"
	val Name = "name"
	val Press = "press"
	val Related = "related"
	val StudyType = "studyType"
	val Tags = "tags"
	val Video = "video"
	def apply(ci:ContentInfo,name:String,headline:String,description:String):CaseStudy =
		CaseStudy(
			ci,
			name,
			headline,
			"",
			List[String](),
			List[CaseStudy](),
			null,
			CopyAsset(ContentInfo(ci.modifiedBy),CaseStudy.Description,scala.xml.XML.loadString(description)),
			List[ImageAsset](),
			List[DownloadableAsset](),
			List[PressAsset]()
		)
}
/** Represent a collection of CaseStudy objects */
case class Client(contentInfo:ContentInfo, name:String, caseStudies:Iterable[CaseStudy]) extends Content {
	def cp(uuid:String) = Client(contentInfo.cp(uuid), name, caseStudies)
}

object Client {
	val NodeType = "wk:client"
	val Name = "name"
	val CaseStudies = "caseStudies"
}
