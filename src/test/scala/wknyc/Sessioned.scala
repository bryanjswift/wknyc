package wknyc

trait Sessioned {
	def sessioned(f: => Any) = {
		// open session so repository doesn't shut down during test
		val session = Config.Repository.login(Config.Admin,Config.CredentialsWorkspace)
		try {
			f
		} finally {
			// logout so repository can shut down
			session.logout
		}
	}
}
