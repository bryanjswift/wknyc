package wknyc.business

import wknyc.Config
import wknyc.WkPredef._
import wknyc.model.{CaseStudy,CaseStudyStatus => Status,ContentInfo,User}
import wknyc.persistence.CaseStudyDao
import wknyc.business.validators.{CaseStudyValidator,Error,ValidationError,ValidationResult}

object CaseStudyManager extends Manager {
	//def get(uuid:String,user:Option[User]) = using(new CaseStudyDao(user.getOrElse(Config.Admin))) { _.get(uuid) }
	def get(uuid:String,user:Option[User]) =
		using(new CaseStudyDao(user.getOrElse(Config.Admin)))(dao =>
			try {
				Success(dao.get(uuid))
			} catch {
				case e:Exception => Failure(List(Error(e)),"Unable to retrieve CaseStudy " + uuid)
			}
		)
	def list = using(new CaseStudyDao(Config.Admin)) { _.list }
	def listNeedsArt = list.filter(cs => cs.status == Status.New || cs.status == Status.CopyComplete)
	def listNeedsCopy = list.filter(cs => cs.status == Status.New || cs.status == Status.ArtComplete)
	private def save[T <: CaseStudy](caseStudy:T,loggedIn:User):Response[CaseStudy] = {
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
