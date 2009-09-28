package wknyc.model

import scala.xml.NodeSeq

class Office(val contentInfo:ContentInfo, val location:String)
class Job(val contentInfo:ContentInfo,
	val title:String,
	val office:Office,
	val department:String,
	val description:NodeSeq,
	val responsibilities:List[String],
	val requirements:List[String],
	val years:Int,
	val education:String
) extends Content