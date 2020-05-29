package fpp.compiler.codegen

import fpp.compiler.ast._
import fpp.compiler.util._

/** Write an FPP annotation as XML */
object AnnotationXmlWriter extends LineUtils {

  /** Write a multiline comment */
  def multilineComment[T](a: Ast.Annotated[T]): List[Line] = {
    (a._1 ++ a._3).map(line) match {
      case Nil => Nil
      case ls => {
        val tags = XmlTags.tags("comment")
        XmlTags.taggedLines(tags)(ls)
      }
    }
  }

  /** Write a single-line comment */
  def singleLineComment[T](a: Ast.Annotated[T]): Option[(String, String)] = {
    val s = (a._1 ++ a._3).mkString(" ")
    if (s.length > 0) Some(("comment", s)) else None
  }

}
