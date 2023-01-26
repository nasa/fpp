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

  def getPortConstants: List[CppDoc.Class.Member] = {
    List(
      getPortEnum(specialInputPorts),
      getPortEnum(typedInputPorts),
      getPortEnum(serialInputPorts),
      getPortEnum(specialOutputPorts),
      getPortEnum(typedOutputPorts),
      getPortEnum(serialOutputPorts),
    ).flatten
  }

  def getPortPublicFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      inputPortWriter.getGetters(specialInputPorts),
      inputPortWriter.getGetters(typedInputPorts),
      inputPortWriter.getGetters(serialInputPorts),
      outputPortWriter.getTypedConnectors(specialOutputPorts),
      outputPortWriter.getTypedConnectors(typedOutputPorts),
      outputPortWriter.getSerialConnectors(serialOutputPorts)
    ).flatten
  }

  def getPortProtectedFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      getPortNumGetters(specialInputPorts),
      getPortNumGetters(typedInputPorts),
      getPortNumGetters(serialInputPorts),
      getPortNumGetters(specialOutputPorts),
      getPortNumGetters(typedOutputPorts),
      getPortNumGetters(serialOutputPorts),
      outputPortWriter.getConnectionStatusQueries(specialOutputPorts),
      outputPortWriter.getConnectionStatusQueries(typedOutputPorts),
      outputPortWriter.getConnectionStatusQueries(serialOutputPorts),
      inputPortWriter.getHandlers(specialInputPorts),
      inputPortWriter.getHandlerBases(specialInputPorts),
      inputPortWriter.getHandlers(typedInputPorts),
      inputPortWriter.getHandlerBases(typedInputPorts),
      inputPortWriter.getHandlers(serialInputPorts),
      inputPortWriter.getHandlerBases(serialInputPorts),
      outputPortWriter.getInvokers(specialOutputPorts),
      outputPortWriter.getInvokers(typedOutputPorts),
      outputPortWriter.getInvokers(serialOutputPorts),
    ).flatten
  }

  def getPortPrivateFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      inputPortWriter.getPreMsgHooks(typedAsyncInputPorts),
      inputPortWriter.getPreMsgHooks(serialAsyncInputPorts),
      inputPortWriter.getCallbacks(specialInputPorts),
      inputPortWriter.getCallbacks(typedInputPorts),
      inputPortWriter.getCallbacks(serialInputPorts)
    ).flatten
  }

  def getPortMemberVariables: List[CppDoc.Class.Member] = {
    List(
      getPortMembers(specialInputPorts),
      getPortMembers(typedInputPorts),
      getPortMembers(serialInputPorts),
      getPortMembers(specialOutputPorts),
      getPortMembers(typedOutputPorts),
      getPortMembers(serialOutputPorts),
    ).flatten
  }

  private def getPortEnum(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            Line.blank :: lines(
              s"//! Enumerations for numbers of ${getTypeString(ports.head)} ${ports.head.getDirection.get.toString} ports"
            ),
            wrapInEnum(
              lines(
                ports.map(p =>
                  writeEnumeratedConstant(
                    portEnumName(p.getUnqualifiedName, p.getDirection.get),
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

  private def getPortNumGetters(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                s"Getters for numbers of ${getTypeString(ports.head)} ${ports.head.getDirection.get.toString} ports"
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

  private def getPortMembers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PRIVATE"),
            CppDocWriter.writeBannerComment(
              s"${getTypeString(ports.head).capitalize} ${ports.head.getDirection.get.toString} ports"
            ),
            ports.flatMap(p => {
              val typeName = getQualifiedPortTypeName(p, p.getDirection.get)
              val name = portMemberName(p.getUnqualifiedName, p.getDirection.get)
              val num = portEnumName(p.getUnqualifiedName, p.getDirection.get)

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

}
