import java.io.FileInputStream
import org.apache.jackrabbit.api.JackrabbitNodeTypeManager
import wknyc.Config
import wknyc.model._

object CndTest {
	val session = Config.Repository.login(WkCredentials("admin@wk.com","","","",None))
	def start = {
		val manager = session.getWorkspace().getNodeTypeManager().asInstanceOf[JackrabbitNodeTypeManager]
		val path = "/Volumes/Freelance/WiedenKennedy/Code/wknyc/src/main/resources/wknyc/nodetype/types.cnd"
		manager.registerNodeTypes(new FileInputStream(path),JackrabbitNodeTypeManager.TEXT_X_JCR_CND)
	}
	def stop = session.logout
}
