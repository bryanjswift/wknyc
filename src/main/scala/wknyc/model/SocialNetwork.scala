package wknyc.model

/** Represents a social networking site */
class SocialNetwork(val name:String, val url:String) {
	private def canEqual(a:Any) = a.isInstanceOf[SocialNetwork]
	def equals(sn:SocialNetwork) =
		this.name == sn.name && this.url == sn.url
	override def equals(q:Any) =
		q match {
			case that:SocialNetwork =>
				canEqual(q) && equals(that)
			case _ => false
		}
	override def hashCode =
		41 * (41 + name.hashCode) + url.hashCode
	override def toString = (new StringBuilder("SocialNetwork(")).append(name).append(",").append(url).append(")").toString
}

object SocialNetwork {
	val NodeType = "wk:socialNetwork"
	def apply(name:String,url:String) = new SocialNetwork(name,url)
}
