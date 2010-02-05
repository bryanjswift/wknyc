package wknyc.business

import wknyc.Config
import wknyc.WkPredef._
import wknyc.model.{Client,ContentInfo,User}
import wknyc.persistence.ClientDao
import wknyc.business.validators.{ClientValidator,Error,ValidationError,ValidationResult}

object ClientManager extends Manager {
	//def get(uuid:String) = using(new ClientDao(Config.Admin)) { _.get(uuid) }
	def get(uuid:String) =
		using(new ClientDao(Config.Admin))(dao =>
			try {
				Success(dao.get(uuid))
			} catch {
				case e:Exception => Failure(List(Error(e)),"Unable to retrieve Client " + uuid)
			}
		)
	def getByName(name:String) = list.find(_.name == name)
	def list = using(new ClientDao(Config.Admin)) { _.list }
	/** Save the Client instance
	 * If instance has Empty ContentInfo then populate it before saving
	 * @param client - instance to save
	 * @param loggedIn - user which is currently logged in to the application
	 * @returns Response[Client] with the errors or the updated Client
	 */
	private def save(client:Client,loggedIn:User):Response[Client] = {
		val c =
			if (client.contentInfo == ContentInfo.Empty) {
				client.cp(ContentInfo(loggedIn))
			} else {
				client
			}
		val errors = ClientValidator.errors(c)
		errors match {
			case Nil =>
				using(new ClientDao(loggedIn))(dao =>
					try {
						Success(dao.save(c))
					} catch {
						case e:Exception => Failure(List(Error(e)),"Unable to create/update Client " + client.name)
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
				Failure(List(ValidationError("user","Must be logged in to save a Client")))
		}
}
