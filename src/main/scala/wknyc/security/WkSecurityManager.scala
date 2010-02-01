package wknyc.security

import javax.security.auth.Subject
import org.apache.jackrabbit.core.security.simple.SimpleSecurityManager
import scala.collection.jcl.Conversions._
import wknyc.model.User

class WkSecurityManager extends SimpleSecurityManager {
	override def getUserID(subject:Subject) = {
		val credentials = subject.getPublicCredentials(classOf[User]).toList
		if (credentials.isEmpty)
			super.getUserID(subject)
		else
			credentials.head.asInstanceOf[User].username
	}
}
