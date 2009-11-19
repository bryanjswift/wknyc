package wknyc.business

import wknyc.WkPredef._
import wknyc.model.{Client,User}
import wknyc.persistence.ClientDao
import validators.{ClientValidator,Error,ValidationError,ValidationResult}

object ClientManager extends Manager {
	def get(uuid:String) = using(new ClientDao(Config.Admin)) { _.get(uuid) }
	def getByName(name:String) = list.find(_.name == name)
	def list = using(new ClientDao(Config.Admin)) { _.list }
	def save(client:Client,loggedIn:User):Response[Client] = {
		val errors = ClientValidator.errors(client)
		errors match {
			case Nil =>
				using(new ClientDao(loggedIn))(dao =>
					try {
						Success(dao.save(client))
					} catch {
						case e:Exception => Failure(List(Error(e)),"Unable to create/update CaseStudy " + client.name)
					}
				)
			case _ =>
				Failure(errors)
		}
	}
	def save(client:Client,u:Option[User]):Response[Client] =
		u match {
			case Some(user) =>
				save(client,user)
			case None =>
				Failure(List(ValidationError("user","Must be logged in to save a Case Study")))
		}
}
