package wknyc.business

// relative to wknyc
import wknyc.Config
import wknyc.WkPredef._
import wknyc.model.{CaseStudy,ContentInfo,User}
import wknyc.persistence.CaseStudyDao
import validators.{CaseStudyValidator,Error,ValidationError,ValidationResult}

object CaseStudyManager extends Manager {
	def get(uuid:String,user:Option[User]) =
		using(new CaseStudyDao(user.getOrElse(Config.Admin)))(dao =>
			try {
				Success(dao.get(uuid))
			} catch {
				case e:Exception => Failure(List(Error(e)),"Unable to retrieve CaseStudy " + uuid)
			}
		)
	def save[T <: CaseStudy](caseStudy:T,loggedIn:User):Response[CaseStudy] = {
		val study =
			if (caseStudy.contentInfo == ContentInfo.Empty)
				caseStudy.cp(ContentInfo(loggedIn))
			else
				caseStudy
		val errors = CaseStudyValidator.errors(study)
		errors match {
			case Nil =>
				using(new CaseStudyDao(loggedIn))(dao =>
					try {
						Success(dao.save(study))
					} catch {
						case e:Exception => Failure(List(Error(e)),"Unable to create/update CaseStudy " + study.name)
					}
				)
			case _ =>
				Failure(errors)
		}
	}
	def save[T <: CaseStudy](study:T,u:Option[User]):Response[CaseStudy] =
		u match {
			case Some(user) =>
				save(study,user)
			case None =>
				Failure(List(ValidationError("user","Must be logged in to save a Case Study")))
		}
}
