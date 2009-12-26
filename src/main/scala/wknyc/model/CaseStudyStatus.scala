package wknyc.model

sealed abstract class CaseStudyStatus(val id:Long)

object CaseStudyStatus {
	case object New extends CaseStudyStatus(0L)
	case object CopyComplete extends CaseStudyStatus(1L)
	case object ArtComplete extends CaseStudyStatus(2L)
	case object Complete extends CaseStudyStatus(3L)
	case object Published extends CaseStudyStatus(4L)
	def apply(id:Long) =
		id match {
			case New.id => New
			case CopyComplete.id => CopyComplete
			case ArtComplete.id => ArtComplete
			case Complete.id => Complete
			case Published.id => Published
		}
}

