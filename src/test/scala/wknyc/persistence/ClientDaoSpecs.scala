package wknyc.persistence

import org.specs.Specification
import wknyc.Config
import wknyc.model.{CaseStudy,Client,ContentInfo,CopyAsset,DownloadableAsset,PressAsset,WkCredentials}

object ClientDaoSpecs extends Specification {
	"ClientDao" should {
		shareVariables()
		setSequential()
		val securitySession = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		val session = Config.Repository.login(Config.Admin,Config.ContentWorkspace)
		val userDao = new UserDao(securitySession,Config.Admin)
		val root = userDao.save(WkCredentials("root@wk.com","root","","",None))
		val assetDao = new AssetDao(session,root)
		val copy = assetDao.save(CopyAsset(
				ContentInfo(root),
				"Title",
				<p>Just a test</p>
			))
		val download = assetDao.save(DownloadableAsset(
				ContentInfo(root),
				"Download Title",
				"/download/path",
				"download url"
			))
		val press = assetDao.save(PressAsset(
				ContentInfo(root),
				"Press Title",
				"Press Author",
				"Press Source Url?",
				"Press Source Name"
			))
		val caseStudy =
			CaseStudy(
				ContentInfo(root),
				copy,
				List(download),
				"Headline",
				"name",
				List(press),
				List[CaseStudy](),
				"study type",
				List("tag1","tag2","another tag")
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
			val caseStudy1 = dao.save(caseStudy)
			caseStudy1.uuid must beSome[String]
			val retrieved = dao.getCaseStudy(caseStudy1.uuid.get)
			caseStudy1 must_== retrieved
		}
		"save a Client" >> {
			val caseStudy1 = dao.save(caseStudy)
			caseStudy1.uuid must beSome[String]
			val client =
				dao.save(Client(
					ContentInfo(root),
					"Test Client",
					List(caseStudy1)
				))
			client.uuid must beSome[String]
		}
		"get a Client" >> {
			val caseStudy1 = dao.save(caseStudy)
			caseStudy1.uuid must beSome[String]
			val client =
				dao.save(Client(
					ContentInfo(root),
					"Test Client",
					List(caseStudy1)
				))
			client.uuid must beSome[String]
			val retrieved = dao.getClient(client.uuid.get)
			client must_== retrieved
		}
	}
}
