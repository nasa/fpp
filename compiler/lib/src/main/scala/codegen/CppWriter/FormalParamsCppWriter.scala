package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Write formal parameters as C++ */
case class FormalParamsWriter(
  s: CppWriterState
) {

  /** Writes a formal parameter as a C++ parameter */
  def writeFormalParam(
    param: Ast.FormalParam,
    strName: Option[String] = None,
    namespaceNames: List[String] = Nil,
    passingConvention: FormalParamsCppWriter.SerializablePassingConvention = FormalParamsCppWriter.ConstRef
  ): String = {
    val t = s.a.typeMap(param.typeName.id)
    val typeName = TypeCppWriter(s, strName, namespaceNames).write(t)

    param.kind match {
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
  }

  /** Writes a list of formal parameters as a list of CppDoc Function Params */
  def writeFormalParamList(
    params: Ast.FormalParamList,
    namespaceNames: List[String] = Nil,
    strName: Option[String] = None,
    passingConvention: FormalParamsCppWriter.SerializablePassingConvention = FormalParamsCppWriter.ConstRef
  ): List[CppDoc.Function.Param] =
    params.map(aNode => {
      CppDoc.Function.Param(
        CppDoc.Type(writeFormalParam(
          aNode._2.data,
          strName,
          namespaceNames,
          passingConvention
        )),
        aNode._2.data.name,
        AnnotationCppWriter.asStringOpt(aNode)
      )
    })

}

object FormalParamsCppWriter {

  /** The passing convention for a serializable type */
  sealed trait SerializablePassingConvention

  case object ConstRef extends SerializablePassingConvention

  case object Value extends SerializablePassingConvention

}
