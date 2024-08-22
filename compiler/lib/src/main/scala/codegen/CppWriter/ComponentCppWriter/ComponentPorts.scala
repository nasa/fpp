package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component port instances */
case class ComponentPorts(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val inputPortWriter = ComponentInputPorts(s, aNode)

  private val outputPortWriter = ComponentOutputPorts(s, aNode)

  def getConstantMembers: List[CppDoc.Class.Member] = {
    List(
      getConstants(specialInputPorts),
      getConstants(typedInputPorts),
      getConstants(serialInputPorts),
      getConstants(specialOutputPorts),
      getConstants(typedOutputPorts),
      getConstants(serialOutputPorts),
    ).flatten
  }

  def getPublicFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      inputPortWriter.getGetters(specialInputPorts),
      inputPortWriter.getGetters(typedInputPorts),
      inputPortWriter.getGetters(serialInputPorts),
      outputPortWriter.getTypedConnectors(specialOutputPorts),
      outputPortWriter.getTypedConnectors(typedOutputPorts),
      outputPortWriter.getSerialConnectors(specialOutputPorts),
      outputPortWriter.getSerialConnectors(typedOutputPorts),
      outputPortWriter.getSerialConnectors(serialOutputPorts),
    ).flatten
  }

  def getProtectedFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      getNumGetters(specialInputPorts),
      getNumGetters(typedInputPorts),
      getNumGetters(serialInputPorts),
      getNumGetters(specialOutputPorts),
      getNumGetters(typedOutputPorts),
      getNumGetters(serialOutputPorts),
      outputPortWriter.getConnectionStatusQueries(specialOutputPorts),
      outputPortWriter.getConnectionStatusQueries(typedOutputPorts),
      outputPortWriter.getConnectionStatusQueries(serialOutputPorts),
      inputPortWriter.getHandlerBases(dataProductInputPorts),
      inputPortWriter.getHandlers(typedInputPorts),
      inputPortWriter.getHandlerBases(typedInputPorts),
      inputPortWriter.getHandlers(serialInputPorts),
      inputPortWriter.getHandlerBases(serialInputPorts),
      inputPortWriter.getPreMsgHooks(dataProductAsyncInputPorts),
      inputPortWriter.getPreMsgHooks(typedAsyncInputPorts),
      inputPortWriter.getPreMsgHooks(serialAsyncInputPorts),
      inputPortWriter.getOverflowHooks(dataProductHookPorts),
      inputPortWriter.getOverflowHooks(typedHookPorts),
      inputPortWriter.getOverflowHooks(serialHookPorts),
      outputPortWriter.getInvokers(dataProductOutputPorts),
      outputPortWriter.getInvokers(typedOutputPorts),
      outputPortWriter.getInvokers(serialOutputPorts),
    ).flatten
  }

  def getPrivateFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      inputPortWriter.getCallbacks(specialInputPorts),
      inputPortWriter.getCallbacks(typedInputPorts),
      inputPortWriter.getCallbacks(serialInputPorts)
    ).flatten
  }

  def getVariableMembers: List[CppDoc.Class.Member] = {
    List(
      getVariables(specialInputPorts),
      getVariables(typedInputPorts),
      getVariables(serialInputPorts),
      getVariables(specialOutputPorts),
      getVariables(typedOutputPorts),
      getVariables(serialOutputPorts),
    ).flatten
  }

  private def getConstants(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      linesClassMember(
        List(
          Line.blank :: lines(
            s"//! Enumerations for numbers of ${getPortListTypeString(ports)} ${ports.head.getDirection.get.toString} ports"
          ),
          wrapInEnum(
            ports.flatMap(p =>
              writeEnumConstant(
                portConstantName(p),
                p.getArraySize,
              )
            )
          )
        ).flatten
      )
    )
  }

  def generateNumGetters(
    ports: List[PortInstance],
    portName: PortInstance => String,
    numGetterName: PortInstance => String,
    variableName: PortInstance => String
  ) = {
    mapPorts(ports, p => List(
      functionClassMember(
        Some(
          s"""|Get the number of ${portName(p)} ports
              |
              |\\return The number of ${portName(p)} ports
              |"""
        ),
        numGetterName(p),
        Nil,
        CppDoc.Type("FwIndexType"),
        lines(
          s"return static_cast<FwIndexType>(FW_NUM_ARRAY_ELEMENTS(this->${variableName(p)}));"
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    ))
  }

  private def getNumGetters(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    val dirStr = ports match {
      case Nil => ""
      case _ => ports.head.getDirection.get.toString
    }

    addAccessTagAndComment(
      "PROTECTED",
      s"Getters for numbers of ${getPortListTypeString(ports)} $dirStr ports",
      generateNumGetters(
        ports,
        (p: PortInstance) => s"${p.getUnqualifiedName} ${p.getDirection.get.toString}",
        portNumGetterName,
        portVariableName
      )
    )
  }

  private def getVariables(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    val dirStr = ports match {
      case Nil => ""
      case _ => ports.head.getDirection.get.toString
    }

    addAccessTagAndComment(
      "PRIVATE",
      s"${getPortListTypeString(ports).capitalize} $dirStr ports",
      mapPorts(ports, p => {
        val typeName = getQualifiedPortTypeName(p, p.getDirection.get)
        val name = portVariableName(p)
        val num = portConstantName(p)

        List(
          linesClassMember(
            lines(
              s"""|
                  |//! ${p.getDirection.get.toString.capitalize} port ${p.getUnqualifiedName}
                  |$typeName $name[$num];
                  |"""
            )
          )
        )
      }, CppDoc.Lines.Hpp),
      CppDoc.Lines.Hpp
    )
  }

  // Get the name for a port enumerated constant
  private def portConstantName(p: PortInstance) =
    s"NUM_${p.getUnqualifiedName.toUpperCase}_${p.getDirection.get.toString.toUpperCase}_PORTS"

}
