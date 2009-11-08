package wknyc.web

import wknyc.model.User

case class WknycSession(user:Option[User])

object WknycSession {
	val Key = "WknycSession"
}
