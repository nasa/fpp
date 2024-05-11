package fpp.compiler.codegen

import fpp.compiler.analysis.*
import fpp.compiler.ast.*
import fpp.compiler.codegen
import fpp.compiler.util.*

/** Write C++ string classes of given sizes */
case class StringCppWriter(
  /** CppWriterState */
  s: CppWriterState
) extends CppWriterUtils {

  /** Get max string size */
  def getSize(str: Type.String): Int = str.size match {
    case Some(typeNode) => s.a.valueMap(typeNode.id) match {
      case Value.EnumConstant(value, _) => value._2.toInt
      case Value.PrimitiveInt(value, _) => value.toInt
      case Value.Integer(value) => value.toInt
      case _ => s.defaultStringSize
    }
    case None => s.defaultStringSize
  }

}
