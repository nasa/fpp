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
      outputPortWriter.getSerialConnectors(serialOutputPorts)
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
      inputPortWriter.getHandlers(typedInputPorts),
      inputPortWriter.getHandlerBases(typedInputPorts),
      inputPortWriter.getHandlers(serialInputPorts),
      inputPortWriter.getHandlerBases(serialInputPorts),
      inputPortWriter.getPreMsgHooks(typedAsyncInputPorts),
      inputPortWriter.getPreMsgHooks(serialAsyncInputPorts),
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
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            Line.blank :: lines(
              s"//! Enumerations for numbers of ${getPortTypeString(ports.head)} ${ports.head.getDirection.get.toString} ports"
            ),
            wrapInEnum(
              lines(
                ports.map(p =>
                  writeEnumConstant(
                    portConstantName(p.getUnqualifiedName, p.getDirection.get),
                    p.getArraySize,
                  )
                ).mkString("\n")
              )
            )
          ).flatten
        )
      )
    )
  }

  private def getNumGetters(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                s"Getters for numbers of ${getPortTypeString(ports.head)} ${ports.head.getDirection.get.toString} ports"
              )
            ).flatten
          )
        )
      ),
      ports.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(
              s"""|Get the number of ${p.getUnqualifiedName} ${p.getDirection.get.toString} ports
                  |
                  |\\return The number of ${p.getUnqualifiedName} ${p.getDirection.get.toString} ports
                  |"""
            ),
            portNumGetterName(p.getUnqualifiedName, p.getDirection.get),
            Nil,
            CppDoc.Type("NATIVE_INT_TYPE"),
            Nil
          )
        )
      )
    ).flatten
  }

  private def getVariables(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PRIVATE"),
            CppDocWriter.writeBannerComment(
              s"${getPortTypeString(ports.head).capitalize} ${ports.head.getDirection.get.toString} ports"
            ),
            ports.flatMap(p => {
              val typeName = getQualifiedPortTypeName(p, p.getDirection.get)
              val name = portVariableName(p.getUnqualifiedName, p.getDirection.get)
              val num = portConstantName(p.getUnqualifiedName, p.getDirection.get)

              lines(
                s"""|
                    |//! ${p.getDirection.get.toString.capitalize} port ${p.getUnqualifiedName}
                    |$typeName $name[$num];
                    |"""
              )
            })
          ).flatten
        )
      )
    )
  }

  // Get the name for a port enumerated constant
  private def portConstantName(name: String, direction: PortInstance.Direction) =
    s"NUM_${name.toUpperCase}_${direction.toString.toUpperCase}_PORTS"

}
