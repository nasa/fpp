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

  def getConstantMembers: List[CppDoc.Class.Member] = List.concat(
    getConstants(specialInputPorts),
    getConstants(typedInputPorts),
    getConstants(serialInputPorts),
    getConstants(specialOutputPorts),
    getConstants(typedOutputPorts),
    getConstants(serialOutputPorts),
  )

  def getPublicFunctionMembers: List[CppDoc.Class.Member] = List.concat(
    inputPortWriter.getGetters(specialInputPorts),
    inputPortWriter.getGetters(typedInputPorts),
    inputPortWriter.getGetters(serialInputPorts),
    outputPortWriter.getTypedConnectors(specialOutputPorts),
    outputPortWriter.getTypedConnectors(typedOutputPorts),
    outputPortWriter.getSerialConnectors(specialOutputPorts),
    outputPortWriter.getSerialConnectors(typedOutputPorts),
    outputPortWriter.getSerialConnectors(serialOutputPorts),
  )

  def getProtectedFunctionMembers: List[CppDoc.Class.Member] = List.concat(
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
  )

  def getPrivateFunctionMembers: List[CppDoc.Class.Member] = List.concat(
    inputPortWriter.getCallbacks(specialInputPorts),
    inputPortWriter.getCallbacks(typedInputPorts),
    inputPortWriter.getCallbacks(serialInputPorts),
    {
      val ports = List(
        guardedOption (hasCommands || hasParameters) (cmdRegPort),
        guardedOption (hasCommands || hasParameters) (cmdRespPort),
        guardedOption (hasEvents) (eventPort),
        guardedOption (hasEvents) (textEventPort),
        timeGetPort,
        guardedOption (hasTelemetry) (tlmPort),
      ).filter(_.isDefined).map(_.get).sortBy(_.getUnqualifiedName)
      outputPortWriter.getInvokers(ports, "private", Some("special"))
    }
  )

  def getVariableMembers: List[CppDoc.Class.Member] = List.concat(
    getVariables(specialInputPorts),
    getVariables(typedInputPorts),
    getVariables(serialInputPorts),
    getVariables(specialOutputPorts),
    getVariables(typedOutputPorts),
    getVariables(serialOutputPorts),
  )

  private def getConstants(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    lazy val member = {
      val kind = getPortListTypeString(ports)
      val direction = ports.head.getDirection.get.toString
      def enumConstant(p: PortInstance) =
        writeEnumConstant(portConstantName(p), p.getArraySize)
      linesClassMember(
        Line.blank ::
        line(s"//! Enumerations for numbers of $kind $direction ports") ::
        wrapInEnum(ports.flatMap(enumConstant))
      )
    }
    guardedList (!ports.isEmpty) (List(member))
  }

  def generateNumGetters(
    ports: List[PortInstance],
    portName: PortInstance => String,
    numGetterName: PortInstance => String,
    ioe: ComponentCppWriterUtils.InternalOrExternal =
      ComponentCppWriterUtils.InternalOrExternal.Internal
  ) = {
    lazy val constantPrefix = ioe match {
      case ComponentCppWriterUtils.InternalOrExternal.Internal => ""
      case ComponentCppWriterUtils.InternalOrExternal.External =>
        s"$componentClassName::"
    }
    def generateNumGetter(p: PortInstance) = lines(
      s"""|
          |//! Get the number of ${portName(p)} ports
          |//!
          |//! \\return The number of ${portName(p)} ports
          |static constexpr FwIndexType ${numGetterName(p)}() {
          |  return ${constantPrefix}${portConstantName(p)};
          |}
          |"""
    )
    mapPorts(
      ports,
      p => List(linesClassMember(generateNumGetter(p))),
      CppDoc.Lines.Hpp
    )
  }

  private def getNumGetters(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    lazy val direction = ports.headOption.map(_.getDirection.get.toString).getOrElse("")
    lazy val kind = getPortListTypeString(ports)
    def portName(p: PortInstance) =
      s"${p.getUnqualifiedName} ${p.getDirection.get.toString}"
    addAccessTagAndComment(
      "protected",
      s"Getters for numbers of $kind $direction ports",
      generateNumGetters(ports, portName, portNumGetterName),
      CppDoc.Lines.Hpp
    )
  }

  private def getVariables(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    lazy val direction = ports.headOption.map(_.getDirection.get.toString).getOrElse("")
    def variable(p: PortInstance) = {
      val typeName = getQualifiedPortTypeName(p, p.getDirection.get)
      val name = portVariableName(p)
      val num = portConstantName(p)
      lines(
        s"""|
            |//! ${p.getDirection.get.toString.capitalize} port ${p.getUnqualifiedName}
            |$typeName $name[$num];
            |"""
      )
    }
    wrapClassMembersInIfDirective(
      "#if !FW_DIRECT_PORT_CALLS",
      addAccessTagAndComment(
        "private",
        s"${getPortListTypeString(ports).capitalize} $direction ports",
        mapPorts(
          ports,
          p => List(linesClassMember(variable(p))),
          CppDoc.Lines.Hpp
        ),
        CppDoc.Lines.Hpp
      ),
      CppDoc.Lines.Hpp
    )
  }

  // Get the name for a port enumerated constant
  private def portConstantName(p: PortInstance) =
    s"NUM_${p.getUnqualifiedName.toUpperCase}_${p.getDirection.get.toString.toUpperCase}_PORTS"

}
