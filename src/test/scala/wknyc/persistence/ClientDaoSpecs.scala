package wknyc.persistence

import org.specs.Specification
import wknyc.model._

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
		val caseStudy =
			CaseStudy(
				ContentInfo(root),
				copy,
				List[DownloadableAsset](),
				"Headline",
				"name",
				List[PressAsset](),
				List[CaseStudy](),
				"study type",
				List[String]()
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
	}
}
