import sbt._

class WknycProject(info:ProjectInfo) extends DefaultWebProject(info) {
	// repository locations (for Jersey)
	val javaNet = "Java.net Repository for Maven" at "http://download.java.net/maven/2/"
	// dependencies for compiling
	val commonsCollections = "commons-collections" % "commons-collections" % "3.2.1"
	val commonsLang = "commons-lang" % "commons-lang" % "2.4"
	// Lucene
	val luceneCore = "org.apache.lucene" % "lucene-core" % "2.9.0"
	// Jersey
	val jersey = "com.sun.jersey" % "jersey-server" % "1.1.2-ea"

	// Dependencies for testing
	val junit = "junit" % "junit" % "4.5" % "test->default"
	val specs = "org.scala-tools.testing" % "specs" % "1.6.0" % "test->default"
	// Jetty
	val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.14" % "test->default"
	val jsp = "org.mortbay.jetty" % "jsp-2.1" % "6.1.14" % "test->default"

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

