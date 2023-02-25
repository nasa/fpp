package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Write formal parameters as C++ */
case class FormalParamsCppWriter(
  s: CppWriterState
) {

  /** Writes a list of formal parameters as a list of CppDoc Function Params */
  def write(
    params: Ast.FormalParamList,
    namespaceNames: List[String] = Nil,
    strName: Option[String] = None,
    passingConvention: FormalParamsCppWriter.SerializablePassingConvention = FormalParamsCppWriter.ConstRef
  ): List[CppDoc.Function.Param] =
    params.map(aNode => {
      CppDoc.Function.Param(
        getFormalParamType(
          aNode._2.data,
          strName,
          namespaceNames,
          passingConvention
        ),
        aNode._2.data.name,
        AnnotationCppWriter.asStringOpt(aNode)
      )
    })

  /** Writes a formal parameter as a C++ parameter */
  def getFormalParamType(
    param: Ast.FormalParam,
    strName: Option[String] = None,
    namespaceNames: List[String] = Nil,
    passingConvention: FormalParamsCppWriter.SerializablePassingConvention = FormalParamsCppWriter.ConstRef
  ): CppDoc.Type = {
    val t = s.a.typeMap(param.typeName.id)
    val typeName = TypeCppWriter(s, strName, namespaceNames).write(t)
    val qualifiedTypeName = param.kind match {
      // Reference formal parameters become non-constant C++ reference parameters
      case Ast.FormalParam.Ref => s"$typeName&"
      case Ast.FormalParam.Value => t match {
        // Primitive, non-reference formal parameters become C++ value parameters
        case t if s.isPrimitive(t, typeName) => typeName
        // String formal parameters become constant C++ reference parameters
        case _: Type.String => s"const $typeName&"
        // Serializable formal parameters become C++ value or constant reference parameters
        case _ => passingConvention match {
          case FormalParamsCppWriter.ConstRef => s"const $typeName&"
          case FormalParamsCppWriter.Value => typeName
        }
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
