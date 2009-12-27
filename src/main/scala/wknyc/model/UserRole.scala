package wknyc.model

sealed abstract class UserRole(val id:Long, val display:String)

object UserRole {
	case object Visitor extends UserRole(0L,"Visitor") // this is a catch-all
	case object Art extends UserRole(1L,"Art Director")
	case object Copy extends UserRole(2L,"Copywriter")
	case object HumanResources extends UserRole(3L,"Human Resources")
	case object CuratorAssistant extends UserRole(4L,"Master Curator Assistant")
	case object Curator extends UserRole(5L,"Master Curator")
	val list = List(Visitor,Art,Copy,HumanResources,CuratorAssistant,Curator)
	def apply(id:Long):UserRole =
		id match {
			case Curator.id => Curator
			case CuratorAssistant.id => CuratorAssistant
			case Art.id => Art
			case Copy.id => Copy
			case HumanResources.id => HumanResources
			case _ => Visitor
		}
	def apply(id:String):UserRole =
		try {
			apply(id.toLong)
		} catch {
			case _ => Visitor
		}
}
