package wknyc.persistence

import java.util.Calendar
import org.specs.Specification
import wknyc.model.{BasicCaseStudy,Client,ContentInfo,DownloadableAsset,Image,ImageAsset,ImageSet,PressAsset,TinyThumbnail,WkCredentials}

object CaseStudyDaoSpecs extends Specification {
	"CaseStudyDao" should {
		shareVariables()
		setSequential()
		val securitySession = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		val session = Config.Repository.login(Config.Admin,Config.ContentWorkspace)
		val userDao = new UserDao(securitySession,Config.Admin)
		val root = userDao.save(WkCredentials("root@wk.com","root","","",None))
		val assetDao = new AssetDao(session,root)
		val download = DownloadableAsset(ContentInfo(root),"Download Title","/download/path","download url")
		val press = PressAsset(ContentInfo(root),"Press Title","Press Author","Press Source Url?","Press Source Name")
		val imageAsset = ImageAsset(
				ContentInfo(root),
				"Test Image",
				ImageSet(new Image("/path/to/what","http://example.com/path","alt",TinyThumbnail))
			)
		val client = Client(ContentInfo(root),"Case Study Client",Nil)
		val c = new ClientDao(session,root)
		val csDao = new CaseStudyDao(session,root)
		val invalidCaseStudy =
			BasicCaseStudy(
				ContentInfo(root),client,"name","Headline","Description",Calendar.getInstance,List(download),true,0
			)
		val caseStudy =
			BasicCaseStudy(
				ContentInfo(root),c.save(client),"name","Headline","Description",Calendar.getInstance,List(download),true,0
			)
		doAfterSpec {
			c.close
			csDao.close
			assetDao.close
			securitySession.logout
			session.logout
		}
		"throw an exception when saving a CaseStudy with unsaved Client" >> {
			csDao.save(invalidCaseStudy) must throwA[Exception]
		}
		"save a valid CaseStudy" >> {
			csDao.save(caseStudy).uuid must beSome[String]
		}
		// TODO: save saving asset case study
		"get a CaseStudy" >> {
			val caseStudy1 = csDao.save(caseStudy) // save doesn't update cascaded uuids
			caseStudy1.uuid must beSome[String]
			val retrieved = csDao.get(caseStudy1.uuid.get)
			caseStudy1.uuid must_== retrieved.uuid
		}
	}
}
