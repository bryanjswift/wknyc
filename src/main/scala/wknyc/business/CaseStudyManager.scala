package wknyc.business

// relative to wknyc
import model.{CaseStudy,User}
import persistence.ClientDao
import validators.{CaseStudyValidator,Error,ValidationSuccess}
import WkPredef._

object CaseStudyManager {
	def save[T <: CaseStudy](study:T,loggedIn:User) = {
		val session = Config.Repository.login(loggedIn,Config.ContentWorkspace)
		val errors = CaseStudyValidator.errors(study)
		errors match {
			case Nil =>
				using(session,new ClientDao(session,loggedIn))(dao =>
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
}
