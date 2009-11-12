package wknyc.business.validators

trait Validator {
	// General Regular Expressions
	protected val emptyRE = "^$".r
	// Generic Helpers
	protected def notValidated(field:String) = ValidationSuccess(field)
	protected def required(value:String,field:String) =
		value match {
			case emptyRE(empty) => ValidationError(field,String.format("%s can not be empty.",field))
			case _ => ValidationSuccess(field)
		}
	def validate(v:AnyRef):List[ValidationResult]
}