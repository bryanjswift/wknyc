package wknyc.persistence

import javax.jcr.{ItemNotFoundException,Session}
import org.specs.Specification
import wknyc.model.User

object DaoSpecs extends Specification {
	private class MockDao(session:Session,user:User) extends Dao(session,user) {
		private val securitySession = Config.Repository.login(user,Config.CredentialsWorkspace)
		val userDao = new UserDao(securitySession,user)
		def close {
			securitySession.logout
			userDao.close
		}
	}
	"Dao instances" should {
		val session = Config.Repository.login(Config.Admin,Config.ContentWorkspace)
		val dao = new MockDao(session,Config.Admin)
		doAfter {
			dao.close
			session.logout
		}
		"delete unversioned nodes with uuids" >> {
			val node = session.getRootNode.addNode("just a test",Dao.DefaultNodeType)
			node.addMixin("mix:referenceable")
			node.getParent.save
			val uuid = node.getUUID
			session.getNodeByUUID(uuid) must notBeNull
			dao.delete(uuid)
			session.getNodeByUUID(uuid) must throwA[ItemNotFoundException]
		}
		"delete versioned nodes with uuids" >> {
			val parent = session.getRootNode.addNode("parent")
			parent.getParent.save
			val node = parent.addNode("just a test",Dao.DefaultNodeType)
			node.addMixin("mix:referenceable")
			node.addMixin("mix:versionable")
			node.getParent.save
			val uuid = node.getUUID
			session.getNodeByUUID(uuid) must notBeNull
			dao.delete(uuid)
			session.getNodeByUUID(uuid) must throwA[ItemNotFoundException]
		}
	}
}
