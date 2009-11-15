package wknyc.web

import wknyc.model.User

case class WknycSession(user:User)

object WknycSession {
	val Key = "WknycSession"
}
