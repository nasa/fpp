package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Write formal parameters as C++ */
case class FormalParamsCppWriter(s: CppWriterState) {

  /** Writes a list of formal parameters as a list of CppDoc Function Params */
  def write(
    params: Ast.FormalParamList,
    strName: String
  ): List[CppDoc.Function.Param] =
    params.map(aNode => {
      CppDoc.Function.Param(
        getFormalParamType(aNode._2.data, strName),
        aNode._2.data.name,
        AnnotationCppWriter.asStringOpt(aNode)
      )
    })

  /** Writes a formal parameter as a C++ parameter */
  def getFormalParamType(
    param: Ast.FormalParam,
    strName: String
  ): CppDoc.Type = {
    val t = s.a.typeMap(param.typeName.id)
    val typeName = TypeCppWriter(s, strName).write(t)
    val qualifiedTypeName = param.kind match {
      // Reference formal parameters become non-constant C++ reference parameters
      case Ast.FormalParam.Ref => s"$typeName&"
      case Ast.FormalParam.Value => t.getUnderlyingType match {
        // Primitive, non-reference formal parameters become C++ value parameters
        case t if s.isPrimitive(t, typeName) => typeName
        // Other formal parameters become constant C++ reference parameters
        case _ => s"const $typeName&"
      }
    }

    CppDoc.Type(qualifiedTypeName)
  }

}

object FormalParamsCppWriter {

  /** The passing convention for a serializable type */
  sealed trait SerializablePassingConvention
  case object ConstRef extends SerializablePassingConvention
  case object Value extends SerializablePassingConvention

}
