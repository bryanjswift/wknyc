package wknyc.model

import java.util.Date

trait Asset extends Content {
	val title:String
}

trait FileAsset extends Asset {
	val path:String
	val url:String
}

sealed case class ImageAsset(
	val dateCreated:Date, val lastModified:Date, val modifiedBy:User,
	val title:String, val largeThumbnail:String, val mediumThumbnail:String,
	val smallThumbnail:String, val tinyThumbnail:String
) extends Asset

sealed case class CopyAsset(
	val dateCreated:Date, val lastModified:Date, val modifiedBy:User,
	val title:String, val body:String
) extends Asset

sealed case class DownloadAsset(
	val dateCreated:Date, val lastModified:Date, val modifiedBy:User,
	val title:String, val url:String, val path:String
) extends FileAsset

/*
sealed case class AudioAsset(
	dateCreated:Date, lastModified:Date, modifiedBy:User, title:String, url:String, path:String
) extends DownloadableAsset(dateCreated, lastModified, modifiedBy, title, url, path)
*/
