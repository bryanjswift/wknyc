package wknyc.model

class CaseStudy(
	val assets:List[Asset],
	val headline:String,
	val copy:CopyAsset,
	val press:List[PressAsset],
	val downloads:List[DownloadableAsset],
	val studyType:String,
	val related:List[CaseStudy],
	val tags:List[String]
)

class Client(
	val name:String,
	val caseStudies:List[CaseStudy]
)
