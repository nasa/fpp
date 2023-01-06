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
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("public"),
              CppDocWriter.writeBannerComment(
                s"Getters for ${getTypeString(ports.head)} input ports"
              )
            ).flatten
          )
        )
      ),
      ports.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(
              s"""|Get ${getTypeString(ports.head)} input port at index
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
                  |return &this->${portMemberName(p.getUnqualifiedName, PortInstance.Direction.Input)}[portNum];
                  |"""
            )
          )
        )
      )
    ).flatten
  }

  def getHandlers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                s"Handlers to implement for ${getTypeString(ports.head)} input ports"
              )
            ).flatten
          )
        )
      ),
      ports.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Handler for input port ${p.getUnqualifiedName}"),
            inputPortHandlerName(p.getUnqualifiedName),
            portNumParam :: getFunctionParams(p),
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.PureVirtual
          )
        )
      )
    ).flatten
  }

  def getHandlerBases(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                s"""|Port handler base-class functions for ${getTypeString(ports.head)} input ports.
                   |Call these functions directly to bypass the corresponding ports.
                   |"""
              ),
            ).flatten
          )
        )
      ),
      ports.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Handler base-class function for input port ${p.getUnqualifiedName}"),
            inputPortHandlerBaseName(p.getUnqualifiedName),
            portNumParam :: getFunctionParams(p),
            CppDoc.Type("void"),
            Nil
          )
        )
      )
    ).flatten
  }

  def getCallbacks(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    if ports.isEmpty then Nil
    else {
      val functions =
        ports.map(p =>
          CppDoc.Class.Member.Function(
            CppDoc.Function(
              Some(s"Callback for port ${p.getUnqualifiedName}"),
              inputPortCallbackName(p.getUnqualifiedName),
              List(
                CppDoc.Function.Param(
                  CppDoc.Type("Fw::PassiveComponentBase*"),
                  "callComp",
                  Some("The component instance")
                ),
                portNumParam
              ) ++ getFunctionParams(p),
              CppDoc.Type("void"),
              Nil,
              CppDoc.Function.Static
            )
          )
        )

      List(
        List(
          CppDoc.Class.Member.Lines(
            CppDoc.Lines(
              List(
                CppDocHppWriter.writeAccessTag("PRIVATE"),
                CppDocWriter.writeBannerComment(
                  s"Calls for messages received on ${getTypeString(ports.head)} input ports"
                ),
              ).flatten
            )
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
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PRIVATE"),
              CppDocWriter.writeBannerComment(
                s"""|Pre-message hooks for ${getTypeString(ports.head)} async input ports.
                    |Each of these functions is invoked just before processing a message
                    |on the corresponding port. By default, they do nothing. You can
                    |override them to provide specific pre-message behavior.
                    |"""
              ),
            ).flatten
          )
        )
      ),
      ports.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Pre-message hook for async input port ${p.getUnqualifiedName}"),
            asyncInputPortHookName(p.getUnqualifiedName),
            portNumParam :: getFunctionParams(p),
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.Virtual
          )
        )
      )
    ).flatten
  }

}