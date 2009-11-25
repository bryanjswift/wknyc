package wknyc.model

import java.util.Calendar

trait CaseStudy extends Content with Ordered {
	def client:Client
	def name:String
	def headline:String
	def description:String
	def launch:Calendar
	def downloads:Iterable[DownloadableAsset]
	def published:Boolean
	def video:DownloadableAsset
	def images:Iterable[ImageAsset]
	def press:Iterable[PressAsset]
	def cp(uuid:String):CaseStudy
	def cp(info:ContentInfo):CaseStudy
}

object CaseStudy {
	val NodeType = "wk:caseStudy"
	val Client = "client"
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
	client:Client,
	name:String,
	headline:String,
	description:String,
	launch:Calendar,
	downloads:Iterable[DownloadableAsset],
	published:Boolean,
	position:Long
) extends CaseStudy {
	val video = EmptyFile
	val images = Nil
	val press = Nil
	def cp(uuid:String) =
		BasicCaseStudy(contentInfo.cp(uuid),client,name,headline,description,launch,downloads,published,position)
	def cp(info:ContentInfo) =
		BasicCaseStudy(info,client,name,headline,description,launch,downloads,published,position)
}

case class AssetCaseStudy(
	basic:BasicCaseStudy,
	video:DownloadableAsset,
	images:Iterable[ImageAsset],
	press:Iterable[PressAsset]
) extends CaseStudy {
	val contentInfo = basic.contentInfo
	val client = basic.client
	val name = basic.name
	val headline = basic.headline
	val description = basic.description
	val launch = basic.launch
	val downloads = basic.downloads
	val published = basic.published
	val position = basic.position
	def cp(uuid:String) =
		AssetCaseStudy(basic.cp(uuid),video,images,press)
	def cp(info:ContentInfo) =
		AssetCaseStudy(basic.cp(info),video,images,press)
}

/** Represent a collection of CaseStudy objects */
case class Client(contentInfo:ContentInfo, name:String, caseStudies:Iterable[CaseStudy]) extends Content {
	def cp(uuid:String) = Client(contentInfo.cp(uuid), name, caseStudies)
	def cp(info:ContentInfo) = Client(info,name,caseStudies)
}

object Client {
	val NodeType = "wk:client"
	val Name = "name"
	val CaseStudies = "caseStudies"
}
