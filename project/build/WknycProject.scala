import bjs.jackrabbit.CleanRepositoryPlugin
import bjs.webapp.CleanWebappPlugin
import sbt._

class WknycProject(info:ProjectInfo) extends DefaultWebProject(info) with CleanWebappPlugin with CleanRepositoryPlugin {
	// repository locations (for Jersey)
	val javaNet = "Java.net Repository for Maven" at "http://download.java.net/maven/2/"
	// Repository location for simple-velocity
	val bryanjswift = "Bryan J Swift" at "http://repos.bryanjswift.com/maven2/"

	// Jackrabbit and Dependencies
	val jcr = "javax.jcr" % "jcr" % "1.0"
	val jackrabbit = "org.apache.jackrabbit" % "jackrabbit-core" % "1.6.0"
	val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.5.3"
	// Lucene -- pulled in by jackrabbit dependency
	// Jersey
	val jersey = "com.sun.jersey" % "jersey-server" % "1.1.2-ea"
	// FileUpload
	val fileUpload = "commons-fileupload" % "commons-fileupload" % "1.2.1"
	// Velocity
	val velocity = "org.apache.velocity" % "velocity" % "1.6.2"
	val simpleVelocity = "bryanjswift" % "simple-velocity" % "0.2.1"
	// MySql
	val mysql = "mysql" % "mysql-connector-java" % "5.1.10"

	// Specs
	val specs = "org.scala-tools.testing" % "specs" % "1.6.0" % "test->default"
	val junit = "junit" % "junit" % "4.5" % "test->default"
	// Jetty
	val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.14" % "test->default"
	val jsp = "org.mortbay.jetty" % "jsp-2.1" % "6.1.14" % "test->default"

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
	// remove test classes and test resources from jetty's classpath
	override def jettyClasspath = super.jettyClasspath --- testCompilePath --- testResourcesOutputPath
	// compile with all debugging information - so command line debugger can be used
	override def compileOptions = super.compileOptions ++ List(CompileOption("-g:vars"))
	// include everything except webapp/assets
	override def scanDirectories = (webappPath * DirectoryFilter +++ webappPath * "*.*" --- webappPath * "assets").get.toList
}

