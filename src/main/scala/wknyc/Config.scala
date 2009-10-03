package wknyc

import org.apache.jackrabbit.core.TransientRepository

object Config {
	lazy val Repository = new TransientRepository()
}
