package wknyc.model

import java.util.Date

trait Content {
	val dateCreated:Date
	val lastModified:Date
	val modifiedBy:User
}

trait OrderedContent[T <: OrderedContent[T]] {
	val position:Int
	val siblings:List[T]
}
