package wknyc.business.validators

sealed trait ValidationResult {
	def field:String
	def message:String
}

case class Error(t:Throwable,message:String) extends ValidationResult {
	val field = Error.Field
}

object Error {
	def apply(t:Throwable):Error = Error(t,t.getMessage)
	val Field = "all"
}

case class ValidationError(field:String,message:String) extends ValidationResult

case class ValidationSuccess(field:String) extends ValidationResult {
	val message = ValidationSuccess.SuccessMessage
}

object ValidationSuccess {
	val SuccessMessage = ""
}
