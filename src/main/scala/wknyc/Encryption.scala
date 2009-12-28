package wknyc

import java.security.MessageDigest

sealed abstract class Encryption(private val algorithmName:String) {
	def apply(str:String) = {
		val algorithm = MessageDigest.getInstance(algorithmName)
		algorithm.update(str.getBytes)
		val bytes = algorithm.digest

		val hexString = new StringBuffer
		bytes.foreach(b => { 
			val s = Integer.toHexString(0xFF & b)
			if (s.length == 1) { hexString.append('0') }
			hexString.append(s)
		})
		hexString.toString
	}
}

object MD5 extends Encryption("MD5")
object SHA extends Encryption("SHA-1")
