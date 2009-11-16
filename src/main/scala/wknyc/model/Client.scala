package wknyc.model

import java.util.Calendar

trait CaseStudy extends Content with Ordered {
	def name:String
	def headline:String
	def description:String
	def launch:Calendar
	def downloads:Iterable[DownloadableAsset]
	def published:Boolean
	def cp(uuid:String):CaseStudy
}

object CaseStudy {
	val NodeType = "wk:caseStudy"
	val Name = "name"
	val Headline = "headline"
	val Description = "description"
	val Launch = "launch"
	val Downloads = "downloads"
	val Published = "published"
	val Video = "video"
	val Images = "images"
	val Press = "press"
	// Unused?
	val Related = "related"
	val StudyType = "studyType"
	val Tags = "tags"
}

case class BasicCaseStudy(
	contentInfo:ContentInfo,
	name:String,
	headline:String,
	description:String,
	launch:Calendar,
	downloads:Iterable[DownloadableAsset],
	published:Boolean,
	position:Long
) extends CaseStudy {
	def cp(uuid:String) =
		BasicCaseStudy(contentInfo.cp(uuid),name,headline,description,launch,downloads,published,position)
}

case class AssetCaseStudy(
	basic:BasicCaseStudy,
	video:DownloadableAsset,
	images:Iterable[ImageAsset],
	press:Iterable[PressAsset]
) extends CaseStudy {
	val contentInfo = basic.contentInfo
	val name = basic.name
	val headline = basic.headline
	val description = basic.description
	val launch = basic.launch
	val downloads = basic.downloads
	val published = basic.published
	val position = basic.position
	def cp(uuid:String) =
		AssetCaseStudy(basic.cp(uuid),video,images,press)
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
