import sbt._
import Process._

class WknycProject(info:ProjectInfo) extends DefaultWebProject(info) {
	// repository locations
	val javaNet = "Java.net Repository for Maven" at "http://download.java.net/maven/2/"
	// dependencies for compiling
	val commonsCollections = "commons-collections" % "commons-collections" % "3.2.1"
	val commonsLang = "commons-lang" % "commons-lang" % "2.4"
	// Lucene
	val luceneCore = "org.apache.lucene" % "lucene-core" % "2.4.1"
	// Jersey
	val jersey = "com.sun.jersey" % "jersey-core" % "1.1.2-ea"

	// Dependencies for testing
	val junit = "junit" % "junit" % "4.5" % "test->default"
	val specs = "org.scala-tools.testing" % "specs" % "1.6.0" % "test->default"

	// override looking for jars in ./lib
	override def dependencyPath = "src" / "main" / "lib"
	// override output of war to target/webapp
	override def temporaryWarPath = outputPath / "war"
	// override path to managed dependency cache
	override def managedDependencyPath = "project" / "lib_managed"
}

