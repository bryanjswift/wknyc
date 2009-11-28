package wknyc.persistence

import java.util.Calendar
import org.specs.Specification
import wknyc.model.{CaseStudy,Client,ContentInfo,DownloadableAsset,Image,ImageAsset,ImageSet,PressAsset,TinyThumbnail,WkCredentials}
import wknyc.model.CaseStudyStatus._

object CaseStudyDaoSpecs extends Specification {
	"CaseStudyDao" should {
		shareVariables()
		setSequential()
		val userDao = new UserDao(Config.Admin)
		val root = userDao.save(WkCredentials("root@wk.com","root","","",None))
		val assetDao = new AssetDao(root)
		val download = DownloadableAsset(ContentInfo(root),"Download Title","/download/path","download url")
		val press = PressAsset(ContentInfo(root),"Press Title","Press Author","Press Source Url?","Press Source Name")
		val imageAsset = ImageAsset(
				ContentInfo(root),
				"Test Image",
				ImageSet(new Image("/path/to/what","http://example.com/path","alt",TinyThumbnail))
			)
		val client = Client(ContentInfo(root),"Case Study Client",Nil)
		val cDao = new ClientDao(root)
		val csDao = new CaseStudyDao(root)
		val invalidCaseStudy =
			CaseStudy(ContentInfo(root),client,"name",Calendar.getInstance,"Headline","Description",List(download),New,0)
		val caseStudy =
			CaseStudy(
				ContentInfo(root),cDao.save(client),"name",Calendar.getInstance,"Headline","Description",List(download),New,0
			)
		doAfterSpec {
			assetDao.close
			cDao.close
			csDao.close
			userDao.close
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
		"update an existing CaseStudy" >> {
			val cs1 = csDao.save(caseStudy)
			val cs2 = csDao.save(CaseStudy(
				cs1.contentInfo.modifiedBy(root),cs1.client,"new name",cs1.launch,cs1.headline,cs1.description,cs1.downloads,New,0
			))
			cs2.uuid must beSome[String]
			cs2.uuid must_== cs1.uuid
			val retrieved = csDao.get(cs1.uuid.get)
			cs1.uuid must_== retrieved.uuid
			retrieved.name must_== "new name"
		}
	}
}
