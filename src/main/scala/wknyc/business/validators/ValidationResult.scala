package wknyc.business.validators

sealed trait ValidationResult {
	def field:String
	def message:String
}

case class ValidationError(field:String,message:String) extends ValidationResult
case class ValidationSuccess(field:String) extends ValidationResult {
	val message = ValidationSuccess.SuccessMessage
}

object ValidationSuccess {
	val SuccessMessage = ""
}