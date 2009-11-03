package wknyc.persistence

import org.specs.Specification
import wknyc.Config
import wknyc.model.{AwardAsset,ContentInfo,CopyAsset,DownloadableAsset,Image,ImageAsset,ImageSet,PressAsset,TinyThumbnail,WkCredentials}

object AssetDaoSpecs extends Specification {
	"AssetDao" should {
		shareVariables()
		setSequential()
		val securitySession = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		val session = Config.Repository.login(Config.Admin,Config.ContentWorkspace)
		val userDao = new UserDao(securitySession,Config.Admin)
		val root = userDao.save(WkCredentials("root@wk.com","root","","",None))
		val dao:AssetDao = new AssetDao(session,root)
		val imageAsset = ImageAsset(
				ContentInfo(root),
				"Test Image",
				ImageSet(new Image("/path/to/what","http://example.com/path","alt",TinyThumbnail))
			)
		val copyAsset = CopyAsset(
				ContentInfo(root),
				"Test Copy",
				<p>This is just a test copy block</p>
			)
		val downloadableAsset = DownloadableAsset(
				ContentInfo(root),
				"Test Download",
				"/path/to/download",
				"http://example.com/path"
			)
		val pressAsset = PressAsset(
				ContentInfo(root),
				"Test Press",
				"Rick Castle",
				"http://example.com",
				"Example Name"
			)
		doAfterSpec {
			dao.close
			securitySession.logout
			session.logout
		}
		"save an ImageAsset" >> {
			dao.save(imageAsset).uuid must beSome[String]
		}
		"get an ImageAsset" >> {
			val asset = dao.save(imageAsset)
			asset.uuid must beSome[String]
			val retrieved = dao.get[ImageAsset](asset.uuid.get)
			asset must_== retrieved
		}
		"save a CopyAsset" >> {
			dao.save(copyAsset).uuid must beSome[String]
		}
		"get a saved CopyAsset" >> {
			val asset = dao.save(copyAsset)
			asset.uuid must beSome[String]
			val retrieved = dao.get[CopyAsset](asset.uuid.get)
			asset must_== retrieved
		}
		"save a DownloadableAsset" >> {
			dao.save(downloadableAsset).uuid must beSome[String]
		}
		"get a saved DownloadableAsset" >> {
			val asset = dao.save(downloadableAsset)
			asset.uuid must beSome[String]
			val retrieved = dao.get[DownloadableAsset](asset.uuid.get)
			asset must_== retrieved
		}
		"save a PressAsset" >> {
			dao.save(pressAsset).uuid must beSome[String]
		}
		"get a saved PressAsset" >> {
			val asset = dao.save(pressAsset)
			asset.uuid must beSome[String]
			val retrieved = dao.get[PressAsset](asset.uuid.get)
			asset must_== retrieved
		}
		"save a AwardAsset" >> {
			val image = dao.save(imageAsset)
			val copy = dao.save(copyAsset)
			val asset = AwardAsset(
					ContentInfo(root),
					"Test Award",
					"Rick Castle",
					copy,
					image
				)
			dao.save(asset).uuid must beSome[String]
		}
		"get a saved AwardAsset" >> {
			val image = dao.save(imageAsset)
			val copy = dao.save(copyAsset)
			val asset = dao.save(AwardAsset(
					ContentInfo(root),
					"Test Award",
					"Rick Castle",
					copy,
					image
				))
			asset.uuid must beSome[String]
			val retrieved = dao.get[AwardAsset](asset.uuid.get)
			asset must_== retrieved
		}
	}
}
