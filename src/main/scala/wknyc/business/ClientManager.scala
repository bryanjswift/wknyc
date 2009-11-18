package wknyc.business

import wknyc.WkPredef._
import wknyc.model.{Client,User}
import wknyc.persistence.ClientDao

object ClientManager extends Manager {
	def get(uuid:String) = using(new ClientDao(Config.Admin)) { _.get(uuid) }
	def list = using(new ClientDao(Config.Admin)) { _.list }
	def getByName(name:String) = list.find(_.name == name)
}
