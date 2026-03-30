package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Utilities for writing C++ port definitions */
abstract class PortCppWriterUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends CppWriterUtils {

  type PortParamType = PortCppWriterUtils.PortParamType

  val portNode = aNode._2

  val portAnnotation = AnnotationCppWriter.asString(aNode)

  val portData = portNode.data

  val portSymbol = Symbol.Port(aNode)

  val portName = s.getName(portSymbol)

  val portBufferName = PortCppWriterUtils.getPortBufferName(portName)

  val portSerializerName = PortCppWriterUtils.getPortSerializerName(portName)

  val portFileName = ComputeCppFiles.FileNames.getPort(portName)

  val namespaceIdentList = s.getNamespaceIdentList(portSymbol)

  val typeCppWriter = TypeCppWriter(s, "Fw::StringBase")

  val returnTypeCppWriter = TypeCppWriter(s, "Fw::String")

  val formalParamsCppWriter = FormalParamsCppWriter(s)

  val portParams = portData.params

  val hasStringParams = portParams.exists(
    param => {
      val t = s.a.typeMap(param._2.data.typeName.id)
      t.getUnderlyingType match {
        case _: Type.String => true
        case _ => false
      }
    }
  )

  val linearBufferFunctionParam = CppDoc.Function.Param(
    CppDoc.Type("Fw::LinearBufferBase&"),
    "_buffer",
    Some("The serial buffer")
  )

  val serialBufferFunctionParam = CppDoc.Function.Param(
    CppDoc.Type("Fw::SerialBufferBase&"),
    "_buffer",
    Some("The serial buffer")
  )

  val inputPortClassName = PortCppWriterUtils.getInputPortClassName(portName)

  val outputPortClassName = PortCppWriterUtils.getOutputPortClassName(portName)

  // Write param names as a comma-separated list
  def writeParamNames = PortCppWriterUtils.writeParamNamesWithPrefix ("") (portParams)

  // Write param names appended to a comma-separated list
  def appendParamNames = commaPrefix(writeParamNames)

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

  // Whether the port has a return type
  val hasReturnType = portData.returnType.isDefined

  // Whether the port needs serialization
  val needsSerialization = !hasReturnType

  // Whether the port has a string return type
  val hasStringReturnType = portData.returnType match {
    case Some(typeName) =>
      s.a.typeMap(typeName.id) match {
        case _: Type.String => true
        case _ => false
      }
    case _ => false
  }

  // Write port invocation arguments
  def writeInvocationArgs(args: List[String]) =
    addSeparators (",") (
      line("this->m_comp") ::
      line("this->m_portNum") ::
      args.map(line)
    ).map(indentIn)

}

object PortCppWriterUtils {

  type PortParamType = Ast.Annotated[AstNode[Ast.FormalParam]]

  def getInputPortClassName(name: String) = s"Input${name}Port"

  def getOutputPortClassName(name: String) = s"Output${name}Port"

  /** Gets the name of the port buffer class */
  def getPortBufferName(name: String) = s"${name}PortBuffer"

  /** Gets the name of the port serializer class */
  def getPortSerializerName(name: String) = s"${name}PortSerializer"

  /** Get the name of a port class */
  def getPortName (name: String) (direction: PortInstance.Direction): String =
    direction match {
      case PortInstance.Direction.Input => getInputPortClassName(name)
      case PortInstance.Direction.Output => getOutputPortClassName(name)
    }

  /** Write parameter names with prefix into a list */
  def writeParamNamesWithPrefix (prefix: String) (portParams: List[PortParamType]) =
    portParams.map(p => s"${prefix}${p._2.data.name}").mkString(", ")

}
