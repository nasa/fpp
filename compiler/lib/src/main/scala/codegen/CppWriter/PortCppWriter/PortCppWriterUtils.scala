package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Utilities for writing C++ port definitions */
abstract class PortCppWriterUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends CppWriterUtils {

  val portNode = aNode._2

  val portAnnotation = AnnotationCppWriter.asString(aNode)

  val portData = portNode.data

  val portSymbol = Symbol.Port(aNode)

  val portName = s.getName(portSymbol)

  val portBufferName = PortCppWriter.getPortBufferName(portName)

  val portFileName = ComputeCppFiles.FileNames.getPort(portName)

  val namespaceIdentList = s.getNamespaceIdentList(portSymbol)

  val typeCppWriter = TypeCppWriter(s, "Fw::StringBase")

  val returnTypeCppWriter = TypeCppWriter(s, "Fw::String")

  val formalParamsCppWriter = FormalParamsCppWriter(s)

  val portParams = portData.params

  val bufferFunctionParam = CppDoc.Function.Param(
    CppDoc.Type("Fw::LinearBufferBase&"),
    "_buffer",
    Some("The serial buffer")
  )

  // Param names in a comma-separated list
  def writeParamNames = portParams.map(_._2.data.name).mkString(", ")

  // Param names appended to a comma-separated list
  def appendParamNames = writeParamNames match {
    case "" => ""
    case paramNames => s", $paramNames"
  }

  // Port params as CppDoc Function Params
  val portFunctionParams: List[CppDoc.Function.Param] =
    formalParamsCppWriter.write(portParams, "Fw::StringBase")

  // Return type as a C++ type
  val returnType = portData.returnType match {
    case Some(value) => returnTypeCppWriter.write(s.a.typeMap(value.id))
    case None => "void"
  }

  // Whether the port has params
  val hasParams = !portParams.isEmpty

  // Whether the port has a return value
  val hasReturnValue = portData.returnType.isDefined


}
