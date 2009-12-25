package wknyc

import java.util.logging.Logger
import java.util.Properties
import scala.collection.jcl.Conversions.convertSet

object Props {
	protected val logger = Logger.getLogger(getClass.getName)
	private[this] val properties = new Properties()
	private[this] val ClassLoader = getClass.getClassLoader
	load("wknyc.properties") // if non-local properties exist load them to overwrite local properties
	def apply(property:String) = properties.getProperty(property)
	def objectForProperty[T](property:String) =
		try {
			Class.forName(apply(property)).getConstructor().newInstance().asInstanceOf[T]
		} catch {
			case ex:Exception =>
				logger.warning("Unable to load instance of " + apply(property) + ". A " + ex.getClass.getName + " was thrown")
		}
	def foreach(fcn: (String,String) => Unit) =
		properties.keySet.foreach(key => fcn(key.toString,apply(key.toString)))
	def foreach(filter: (String) => Boolean, fcn: (String,String) => Unit) =
		properties.keySet.filter(key => filter(key.toString)).foreach(key => fcn(key.toString,apply(key.toString)))
	def load(path:String) =
		try {
			properties.load(ClassLoader.getResourceAsStream(path))
		} catch {
			case ex:Exception =>
				logger.warning("Unable to load properties in classpath resource : " + path + ". Attempting it threw a " + ex.getClass.getName)
		}
}
