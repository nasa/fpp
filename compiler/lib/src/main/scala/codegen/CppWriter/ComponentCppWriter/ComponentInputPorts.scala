package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component input port instances */
case class ComponentInputPorts(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getGetters(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        linesClassMember(
          List(
            CppDocHppWriter.writeAccessTag("public"),
            CppDocWriter.writeBannerComment(
              s"Getters for ${getPortTypeString(ports.head)} input ports"
            )
          ).flatten
        )
      ),
      mapPorts(ports, p => List(
        functionClassMember(
          Some(
            s"""|Get ${getPortTypeString(ports.head)} input port at index
                |
                |\\return ${p.getUnqualifiedName}[portNum]
                |"""
          ),
          inputPortGetterName(p.getUnqualifiedName),
          List(
            portNumParam
          ),
          CppDoc.Type(s"${getQualifiedPortTypeName(p, PortInstance.Direction.Input)}*"),
          lines(
            s"""|FW_ASSERT(
                |  portNum < this->${portNumGetterName(p.getUnqualifiedName, PortInstance.Direction.Input)}(),
                |  static_cast<FwAssertArgType>(portNum)
                | );
                |
                |return &this->${portVariableName(p.getUnqualifiedName, PortInstance.Direction.Input)}[portNum];
                |"""
          )
        )
      ))
    ).flatten
  }

  def getHandlers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        linesClassMember(
          List(
            CppDocHppWriter.writeAccessTag("PROTECTED"),
            CppDocWriter.writeBannerComment(
              s"Handlers to implement for ${getPortTypeString(ports.head)} input ports"
            )
          ).flatten
        )
      ),
      ports.map(p =>
        functionClassMember(
          Some(s"Handler for input port ${p.getUnqualifiedName}"),
          inputPortHandlerName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          CppDoc.Type("void"),
          Nil,
          CppDoc.Function.PureVirtual
        )
      )
    ).flatten
  }

  def getHandlerBases(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        linesClassMember(
          List(
            CppDocHppWriter.writeAccessTag("PROTECTED"),
            CppDocWriter.writeBannerComment(
              s"""|Port handler base-class functions for ${getPortTypeString(ports.head)} input ports.
                 |Call these functions directly to bypass the corresponding ports.
                 |"""
            ),
          ).flatten
        )
      ),
      ports.map(p =>
        functionClassMember(
          Some(s"Handler base-class function for input port ${p.getUnqualifiedName}"),
          inputPortHandlerBaseName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          CppDoc.Type("void"),
          Nil
        )
      )
    ).flatten
  }

  def getCallbacks(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else {
      val functions =
        mapPorts(ports, p => List(
          functionClassMember(
            Some(s"Callback for port ${p.getUnqualifiedName}"),
            inputPortCallbackName(p.getUnqualifiedName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type("Fw::PassiveComponentBase*"),
                "callComp",
                Some("The component instance")
              ),
              portNumParam
            ) ++ getPortFunctionParams(p),
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.Static
          )
        ))

      List(
        List(
          linesClassMember(
            List(
              CppDocHppWriter.writeAccessTag("PRIVATE"),
              CppDocWriter.writeBannerComment(
                s"Calls for messages received on ${getPortTypeString(ports.head)} input ports"
              ),
            ).flatten
          )
        ),
        ports.head.getType.get match {
          case PortInstance.Type.DefPort(_) => functions
          case PortInstance.Type.Serial =>
            wrapClassMembersInIfDirective(
              "\n#if FW_PORT_SERIALIZATION",
              functions
            )
        }
      ).flatten
    }
  }

  def getPreMsgHooks(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        linesClassMember(
          List(
            CppDocHppWriter.writeAccessTag("PROTECTED"),
            CppDocWriter.writeBannerComment(
              s"""|Pre-message hooks for ${getPortTypeString(ports.head)} async input ports.
                  |Each of these functions is invoked just before processing a message
                  |on the corresponding port. By default, they do nothing. You can
                  |override them to provide specific pre-message behavior.
                  |"""
            ),
          ).flatten
        )
      ),
      ports.map(p =>
        functionClassMember(
          Some(s"Pre-message hook for async input port ${p.getUnqualifiedName}"),
          inputPortHookName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          CppDoc.Type("void"),
          Nil,
          CppDoc.Function.Virtual
        )
      )
    ).flatten
  }

  // Get the name for an input port getter function
  private def inputPortGetterName(name: String) =
    s"get_${name}_InputPort"

  // Get the name for an input port handler function
  private def inputPortHandlerName(name: String) =
    s"${name}_handler"

  // Get the name for an input port handler base-class function
  private def inputPortHandlerBaseName(name: String) =
    s"${name}_handlerBase"

}