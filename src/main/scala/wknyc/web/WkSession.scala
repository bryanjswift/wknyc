package wknyc.web

import wknyc.model.User

case class WkSession(user:User)

object WkSession {
	val Key = "WknycSession"
}
