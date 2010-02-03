package wknyc.model

sealed abstract class CaseStudyStatus(val id:Long, val display:String)

object CaseStudyStatus {
	case object New extends CaseStudyStatus(0L,"New")
	case object CopyComplete extends CaseStudyStatus(1L, "Copywriter Complete")
	case object ArtComplete extends CaseStudyStatus(2L, "Art Direction Complete")
	case object Complete extends CaseStudyStatus(3L, "Complete")
	case object Published extends CaseStudyStatus(4L, "Published")
	def apply(id:Long) =
		id match {
			case New.id => New
			case CopyComplete.id => CopyComplete
			case ArtComplete.id => ArtComplete
			case Complete.id => Complete
			case Published.id => Published
		}
	def list = List(New,CopyComplete,ArtComplete,Complete,Published)
}

