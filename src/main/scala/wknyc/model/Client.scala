package wknyc.model

case class Client(contentInfo:ContentInfo, name:String, caseStudies:Iterable[CaseStudy]) extends Content {
	def cp(uuid:String) = Client(contentInfo.cp(uuid), name, caseStudies)
	def cp(info:ContentInfo) = Client(info,name,caseStudies)
}

object Client {
	val NodeType = "wk:client"
	val Name = "name"
	val CaseStudies = "caseStudies"
}
