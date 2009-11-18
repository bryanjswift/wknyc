package wknyc.business

import wknyc.WkPredef._
import wknyc.model.{Client,User}
import wknyc.persistence.ClientDao

object ClientManager extends Manager {
	type D = ClientDao
	def get(uuid:String) = {
		val session = Config.Repository.login(Config.Admin,Config.ContentWorkspace)
		using(session,new ClientDao(Config.Admin)) { _.get(uuid) }
	}
}
