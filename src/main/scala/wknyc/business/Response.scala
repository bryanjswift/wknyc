package wknyc.business

import wknyc.business.validators.ValidationResult

sealed trait Response[+T] {
	def errors:List[ValidationResult]
	def message:String
	def payload:T
}

private object Response {
	val EmptyMessage = ""
}

case class Success[T](payload:T,message:String) extends Response[T] {
	val errors = Nil
}

object Success {
	def apply[T](payload:T):Success[T] = Success(payload,Response.EmptyMessage)
}

case class Failure(errors:List[ValidationResult],message:String) extends Response[Nothing] {
	def payload = throw new java.util.NoSuchElementException(Failure.Payload)
}

object Failure {
	def apply(errors:List[ValidationResult]):Failure = Failure(errors,Response.EmptyMessage)
	private val Payload = "Failure.payload"
}
