package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.util._

/** Write an FPP type as XML */
object TypeXmlWriter {

  /** Get the name of a type */
  def getName(s: XmlWriterState, t: Type): String =
    TypeCppWriter.getName(s.cppWriterState, t)

  /** Get the size of a type */
  def getSize(s: XmlWriterState, t: Type): Option[String] = t match {
    case Type.String(Some(node)) => Some(s.a.valueMap(node.id).toString)
    case Type.String(None) => Some(s.defaultStringSize.toString)
    case _ => None
  }

  /** Get the key-value pairs for a type */
  def getPairs(s: XmlWriterState, t: Type, nameTag: String = "type"): List[(String,String)] = {
    val name = (nameTag, getName(s, t))
    getSize(s, t) match {
      case Some(size) => List(name, ("size", size))
      case None => List(name)
    }
  }

}
