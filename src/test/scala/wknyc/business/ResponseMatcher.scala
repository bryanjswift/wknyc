package wknyc.business

import org.specs.matcher.Matcher

case class beSuccess[T]() extends Matcher[Response[_]] {
	def apply(r: => Response[_]) = (r.isInstanceOf[Success[T]],"Is Success","Not Success")
}

case class beFailure() extends Matcher[Response[_]] {
	def apply(r: => Response[_]) = (r.isInstanceOf[Failure],"Is Failure","Not Failure")
}