package wknyc.web.providers

import java.lang.{String, Class}
import javax.ws.rs.core.{MultivaluedMap, MediaType}
import javax.ws.rs.ext.{Provider,MessageBodyWriter}
import java.lang.annotation.Annotation
import java.lang.reflect.Type
import scala.xml.NodeSeq
import javax.ws.rs.Produces
import java.io.OutputStream
import com.sun.jersey.core.util.ReaderWriter

@Provider
@Produces(Array(MediaType.APPLICATION_XML,MediaType.TEXT_PLAIN,MediaType.TEXT_HTML,
	MediaType.TEXT_XML,MediaType.APPLICATION_ATOM_XML,MediaType.APPLICATION_XHTML_XML))
class NodeSeqWriterProvider extends MessageBodyWriter[NodeSeq] {
	def writeTo(t:NodeSeq, c:Class[_], g:Type, a:Array[Annotation], mType:MediaType, headers:MultivaluedMap[String, Object], out:OutputStream) =
		ReaderWriter.writeToAsString(t.toString,out,mType)
	def getSize(t:NodeSeq, c:Class[_], g:Type, annotations:Array[Annotation], mediaType:MediaType) = t.toString.length
	def isWriteable(c:Class[_], g:Type, annotations:Array[Annotation], mediaType:MediaType) = classOf[NodeSeq].isAssignableFrom(c)
}
