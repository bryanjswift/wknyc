package wknyc.model

import java.util.Date

trait Asset extends Content {
	val title:String
}

trait FileAsset extends Asset {
	val path:String
	val url:String
}

class ImageAsset(
	val dateCreated:Date, val lastModified:Date, val modifiedBy:User,
	val title:String, val largeThumbnail:String, val mediumThumbnail:String,
	val smallThumbnail:String, val tinyThumbnail:String
) extends Asset

class CopyAsset(
	val dateCreated:Date, val lastModified:Date, val modifiedBy:User,
	val title:String, val body:String
) extends Asset

class DownloadableAsset(
	val dateCreated:Date, val lastModified:Date, val modifiedBy:User,
	val title:String, val url:String, val path:String
) extends FileAsset

class DownloadAsset(
	dateCreated:Date, lastModified:Date, modifiedBy:User, title:String, url:String, path:String
) extends DownloadableAsset(dateCreated, lastModified, modifiedBy, title, url, path)

class AudioAsset(
	dateCreated:Date, lastModified:Date, modifiedBy:User, title:String, url:String, path:String
) extends DownloadableAsset(dateCreated, lastModified, modifiedBy, title, url, path)
