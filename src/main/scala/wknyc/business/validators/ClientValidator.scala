package wknyc.business.validators

import wknyc.model.Client

object ClientValidator extends Validator {
	def validate(target:AnyRef) =
		target match {
			case client:Client => validateClient(client)
			case _ => throw new IllegalArgumentException(String.format("%s is not a known Client type",target.getClass.getName))
		}
	// Type Validation
	private def validateClient(client:Client) = ( // this is one statement
		validateName(client.name)
		:: Nil
	)
	private def validateName(name:String) = required(name,Client.Name)
}