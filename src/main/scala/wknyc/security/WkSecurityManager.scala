package wknyc.security

import javax.security.auth.Subject
import org.apache.jackrabbit.core.security.simple.SimpleSecurityManager
import scala.collection.jcl.Conversions._
import wknyc.model.WkCredentials

class WkSecurityManager extends SimpleSecurityManager {
	override def getUserID(subject:Subject) = {
		val credentials = subject.getPublicCredentials(classOf[WkCredentials]).toList
		if (credentials.isEmpty)
			super.getUserID(subject)
		else
			credentials(0).asInstanceOf[WkCredentials].username
	}
}
