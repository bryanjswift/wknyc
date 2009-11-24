package wknyc.business.validators

trait Validator {
	case class ValidationResults(all:List[ValidationResult]) {
		private val (success,error) = all.partition(_.isInstanceOf[ValidationSuccess])
		val errors = error.asInstanceOf[List[ValidationError]]
		val successes = success.asInstanceOf[List[ValidationSuccess]]
	}
	// General Regular Expressions
	// Generic Helpers
	protected def notValidated(field:String) = ValidationSuccess(field)
	protected def required(value:String,field:String) =
		value match {
			case null => ValidationError(field,String.format("%s can not be null.",field))
			case "" => ValidationError(field,String.format("%s can not be empty.",field))
			case _ => ValidationSuccess(field)
		}
	// Worker methods
	def validate(a:AnyRef):List[ValidationResult]
	def errors(a:AnyRef) = results(a).errors
	def results(a:AnyRef) = ValidationResults(validate(a))
	def successes(a:AnyRef) = results(a).successes
}
