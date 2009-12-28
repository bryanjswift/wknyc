package wknyc.model

case class Client(contentInfo:ContentInfo, name:String, caseStudies:Iterable[CaseStudy]) extends Content {
	// TODO: Remove these when upgraded to Scala 2.8
	def cp(uuid:Option[String]) = Client(contentInfo.cp(uuid), name, caseStudies)
	def cp(info:ContentInfo) = Client(info,name,caseStudies)
}

object Client {
	val NodeType = "wk:client"
	val Name = "name"
	val CaseStudies = "caseStudies"
}
