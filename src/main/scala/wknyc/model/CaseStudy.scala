package wknyc.model

import java.util.Calendar
import wknyc.model.{CaseStudyStatus => Status}

sealed trait CaseStudy extends Content with Ordered {
	def client:Client
	def name:String
	def headline:String
	def description:String
	def launch:Calendar
	def downloads:Iterable[DownloadableAsset]
	def status:Status
	def video:DownloadableAsset
	def images:Iterable[ImageAsset]
	def press:Iterable[PressAsset]
	def cp(uuid:String):CaseStudy = cp(contentInfo.cp(uuid))
	def cp(info:ContentInfo):CaseStudy = 
		AssetCaseStudy(
			BasicCaseStudy(BaseCaseStudy(info,client,name,launch),headline,description,downloads,status,position),
			video,
			images,
			press
		)
}

object CaseStudy {
	val NodeType = "wk:caseStudy"
	val Client = "client"
	val Name = "name"
	val Headline = "headline"
	val Description = "description"
	val Launch = "launch"
	val Downloads = "downloads"
	val Status = "status"
	val Video = "video"
	val Images = "images"
	val Press = "press"
	// Unused?
	val Related = "related"
	val StudyType = "studyType"
	val Tags = "tags"
	def apply(info:ContentInfo,client:Client,name:String):CaseStudy =
		apply(info,client,name,Calendar.getInstance)
	def apply(info:ContentInfo,client:Client,name:String,launch:Calendar):CaseStudy =
		BaseCaseStudy(info,client,name,launch)
	def apply(
		info:ContentInfo,client:Client,name:String,launch:Calendar,headline:String,description:String,
		downloads:Iterable[DownloadableAsset],status:Status,position:Long):CaseStudy =
			BasicCaseStudy(BaseCaseStudy(info,client,name,launch),headline,description,downloads,status,position)
	def apply(
		info:ContentInfo,client:Client,name:String,launch:Calendar,headline:String,description:String,
		downloads:Iterable[DownloadableAsset],status:Status,position:Long,video:DownloadableAsset,
		images:Iterable[ImageAsset],press:Iterable[PressAsset]):CaseStudy =
			AssetCaseStudy(
				BasicCaseStudy(BaseCaseStudy(info,client,name,launch),headline,description,downloads,status,position),
				video,
				images,
				press
			)
}

private case class BaseCaseStudy(
	contentInfo:ContentInfo,
	client:Client,
	name:String,
	launch:Calendar
) extends CaseStudy {
	val headline = ""
	val description = ""
	val downloads = Nil
	val status = Status.New
	val position = 0L
	val video = EmptyFile
	val images = Nil
	val press = Nil
}

private case class BasicCaseStudy(
	base:BaseCaseStudy,
	headline:String,
	description:String,
	downloads:Iterable[DownloadableAsset],
	status:Status,
	position:Long
) extends CaseStudy {
	val contentInfo = base.contentInfo
	val client = base.client
	val name = base.name
	val launch = base.launch
	val video = base.video
	val images = base.images
	val press = base.press
}

private case class AssetCaseStudy(
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
	val status = basic.status
	val position = basic.position
}
