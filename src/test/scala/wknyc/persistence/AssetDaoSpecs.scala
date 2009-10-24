package wknyc.persistence

import org.specs.Specification
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
		doAfterSpec {
			dao.close
			securitySession.logout
			session.logout
		}
		"save an ImageAsset" >> {
			val asset = ImageAsset(
					ContentInfo(root),
					"Test Image",
					ImageSet(new Image("/path/to/what","http://example.com/path","alt",15,20,TinyThumbnail))
				)
			dao.save(asset).uuid must beSome[String]
		}
		"get an ImageAsset" >> {
			val asset = dao.save(ImageAsset(
					ContentInfo(root),
					"Test Image",
					ImageSet(new Image("/path/to/what","http://example.com/path","alt",15,20,TinyThumbnail))
				))
			asset.uuid must beSome[String]
			val retrieved = dao.get(asset.uuid.get)
			asset must_== retrieved
		}
		"save a CopyAsset" >> {
			val asset = CopyAsset(
					ContentInfo(root),
					"Test Copy",
					<p>This is just a test copy block</p>
				)
			dao.save(asset).uuid must beSome[String]
		}
		"get a saved CopyAsset" >> {
			val asset = dao.save(CopyAsset(
					ContentInfo(root),
					"Test Copy",
					<p>This is just a test copy block</p>
				))
			asset.uuid must beSome[String]
			val retrieved = dao.get(asset.uuid.get)
			asset must_== retrieved
		}
		"save a DownloadableAsset" >> {
			val asset = DownloadableAsset(
					ContentInfo(root),
					"Test Download",
					"/path/to/download",
					"http://example.com/path"
				)
			dao.save(asset).uuid must beSome[String]
		}
		"get a saved DownloadableAsset" >> {
			val asset = dao.save(DownloadableAsset(
					ContentInfo(root),
					"Test Download",
					"/path/to/download",
					"http://example.com/path"
				))
			asset.uuid must beSome[String]
			val retrieved = dao.get(asset.uuid.get)
			asset must_== retrieved
		}
		"save a PressAsset" >> {
			val asset = PressAsset(
					ContentInfo(root),
					"Test Press",
					"Rick Castle",
					"http://example.com",
					"Example Name"
				)
			dao.save(asset).uuid must beSome[String]
		}
		"get a saved PressAsset" >> {
			val asset = dao.save(PressAsset(
					ContentInfo(root),
					"Test Press",
					"Rick Castle",
					"http://example.com",
					"Example Name"
				))
			asset.uuid must beSome[String]
			val retrieved = dao.get(asset.uuid.get)
			asset must_== retrieved
		}
		"save a AwardAsset" >> {
			val image = dao.save(ImageAsset(
					ContentInfo(root),
					"Test Image",
					ImageSet(new Image("/path/to/what","http://example.com/path","alt",15,20,TinyThumbnail))
				))
			val copy = dao.save(CopyAsset(
					ContentInfo(root),
					"Test Copy",
					<p>This is just a test copy block</p>
				))
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
			val image = dao.save(ImageAsset(
					ContentInfo(root),
					"Test Image",
					ImageSet(new Image("/path/to/what","http://example.com/path","alt",15,20,TinyThumbnail))
				))
			val copy = dao.save(CopyAsset(
					ContentInfo(root),
					"Test Copy",
					<p>This is just a test copy block</p>
				))
			val asset = dao.save(AwardAsset(
					ContentInfo(root),
					"Test Award",
					"Rick Castle",
					copy,
					image
				))
			asset.uuid must beSome[String]
			val retrieved = dao.get(asset.uuid.get)
			asset must_== retrieved
		}
	}
}
