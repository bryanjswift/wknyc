package wknyc.model

sealed abstract class CaseStudyStatus(val id:Long)

object CaseStudyStatus {
	case object New extends CaseStudyStatus(0L)
	case object Normal extends CaseStudyStatus(1L)
	case object CopyComplete extends CaseStudyStatus(2L)
	case object ArtComplete extends CaseStudyStatus(3L)
	case object Complete extends CaseStudyStatus(4L)
	case object Published extends CaseStudyStatus(5L)
	def apply(id:Long) =
		id match {
			case New.id => New
			case Normal.id => Normal
			case CopyComplete.id => CopyComplete
			case ArtComplete.id => ArtComplete
			case Complete.id => Complete
			case Published.id => Published
		}
}

