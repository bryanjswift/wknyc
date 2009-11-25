package wknyc.model

object CaseStudyStatus {
	trait Status
	case object Normal extends Status
	case object CopyComplete extends Status
	case object ArtComplete extends Status
	case object Complete extends Status
	case object Published extends Status
}
