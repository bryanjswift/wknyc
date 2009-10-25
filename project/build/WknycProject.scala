import sbt._

class WknycProject(info:ProjectInfo) extends DefaultWebProject(info) {
	// repository locations (for Jersey)
	val javaNet = "Java.net Repository for Maven" at "http://download.java.net/maven/2/"

	// define project for submodule
	lazy val simpleVelocity = project("simple-velocity")

	// Jackrabbit and Dependencies
	val jcr = "javax.jcr" % "jcr" % "1.0"
	val jackrabbit = "org.apache.jackrabbit" % "jackrabbit-core" % "1.6.0"
	val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.5.3"
	// Lucene -- pulled in by jackrabbit dependency
	// Jersey
	val jersey = "com.sun.jersey" % "jersey-server" % "1.1.2-ea"

	// Specs
	val specs = "org.scala-tools.testing" % "specs" % "1.6.0" % "test->default"
	val junit = "junit" % "junit" % "4.5" % "test->default"
	// Jetty
	val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.14" % "test->default"
	val jsp = "org.mortbay.jetty" % "jsp-2.1" % "6.1.14" % "test->default"

	// Define task to delete webapp class and jar files
	lazy val cleanWebapp = task {
		val toClean = webappPath / "WEB-INF" / "classes" ** "*" +++ webappPath / "WEB-INF" / "lib" * "*.jar"
		try {
			toClean.getFiles.foreach(_.delete)
			None
		} catch {
			case e:Exception => 
				Some(e.getMessage)
		}
	}

	// Define task to clean up repository from filesystem
	lazy val cleanRepository = task {
		def deleteDirectory(path:java.io.File):Boolean = {
			path.listFiles.foreach(f => if (f.isDirectory) deleteDirectory(f) else f.delete)
			path.delete
		}
		val repositoryPath = "." / "repository"
		try {
			repositoryPath.getFiles.foreach(deleteDirectory)
			None
		} catch {
			case e:Exception =>
				Some(e.getMessage)
		}
	}
	// override clean action to depend on cleanWebapp
	override def cleanAction = super.cleanAction dependsOn(cleanRepository) dependsOn(cleanWebapp)
	// tell sbt to find files normally under src/main/webapp under the webapp directory
	override def webappPath = "webapp"
	// override looking for jars in ./lib
	override def dependencyPath = "src" / "main" / "lib"
	// override output of exploded war to webapp directory
	// webappPath and temporaryWarPath being the same means WEB-INF/lib doesn't get cleaned
	override def temporaryWarPath = "webapp"
	// override path to managed dependency cache
	override def managedDependencyPath = "project" / "lib_managed"
}

