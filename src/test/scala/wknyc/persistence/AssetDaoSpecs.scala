package wknyc.persistence

import org.specs.Specification
import wknyc.model.{ContentInfo,Image,ImageAsset,ImageSet,TinyThumbnail,WkCredentials}

object AssetDaoSpecs extends Specification {
	"AssetDao" should {
		shareVariables()
		setSequential()
		val securitySession = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		val session = Config.Repository.login(Config.Admin,Config.ContentWorkspace)
		var root = WkCredentials("root@wk.com","root","","",None)
		doAfterSpec {
			securitySession.logout
			session.logout
		}
		"save an ImageAsset" >> {
			val userDao = new UserDao(securitySession,root)
			root = userDao.save(root)
			root.uuid must beSome[String] // Test credential saving
			val dao = new AssetDao(session,root)
			val asset = ImageAsset(
					ContentInfo(root),
					"Test Image",
					new ImageSet(new Image("/path/to/what","http://example.com/path","alt",15,20,TinyThumbnail))
				)
			dao.save(asset).uuid must beSome[String]
		}
	}
}
