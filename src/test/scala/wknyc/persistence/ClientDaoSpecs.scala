package wknyc.persistence

import org.specs.Specification
import wknyc.model.{CaseStudy,Client,ContentInfo,CopyAsset,DownloadableAsset,Image,ImageAsset,ImageSet,PressAsset,TinyThumbnail,WkCredentials}

object ClientDaoSpecs extends Specification {
	"ClientDao" should {
		shareVariables()
		setSequential()
		val securitySession = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		val session = Config.Repository.login(Config.Admin,Config.ContentWorkspace)
		val userDao = new UserDao(securitySession,Config.Admin)
		val root = userDao.save(WkCredentials("root@wk.com","root","","",None))
		val assetDao = new AssetDao(session,root)
		val copy = CopyAsset(
				ContentInfo(root),
				"Title",
				<p>Just a test</p>
			)
		val download = DownloadableAsset(
				ContentInfo(root),
				"Download Title",
				"/download/path",
				"download url"
			)
		val press = PressAsset(
				ContentInfo(root),
				"Press Title",
				"Press Author",
				"Press Source Url?",
				"Press Source Name"
			)
		val imageAsset = ImageAsset(
				ContentInfo(root),
				"Test Image",
				ImageSet(new Image("/path/to/what","http://example.com/path","alt",TinyThumbnail))
			)
		val caseStudy =
			CaseStudy(
				ContentInfo(root),
				"name",
				"Headline",
				"study type",
				List("tag1","tag2","another tag"),
				List[CaseStudy](),
				download,
				copy,
				List(imageAsset),
				List(download),
				List(press)
			)
		val dao = new ClientDao(session,root)
		doAfterSpec {
			dao.close
			assetDao.close
			securitySession.logout
			session.logout
		}
		"save a CaseStudy" >> {
			dao.save(caseStudy).uuid must beSome[String]
		}
		"get a CaseStudy" >> {
			val caseStudy1 = dao.save(caseStudy) // save doesn't update cascaded uuids
			caseStudy1.uuid must beSome[String]
			val retrieved = dao.getCaseStudy(caseStudy1.uuid.get)
			caseStudy1.uuid must_== retrieved.uuid
		}
		"save a Client" >> {
			val client =
				dao.save(Client(
					ContentInfo(root),
					"Test Client",
					List(caseStudy)
				))
			client.uuid must beSome[String]
		}
		"get a Client" >> {
			val client =
				dao.save(Client(
					ContentInfo(root),
					"Test Client",
					List(caseStudy)
				)) // save doesn't update cascaded uuids
			client.uuid must beSome[String]
			val retrieved = dao.getClient(client.uuid.get)
			client.uuid must_== retrieved.uuid
		}
	}
}
