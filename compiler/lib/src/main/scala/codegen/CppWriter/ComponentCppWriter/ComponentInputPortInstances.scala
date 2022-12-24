package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component input port instances */
case class ComponentInputPortInstances (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  // Map from port instance name to a PortCppWriter
  private val portWriterMap = typedInputPorts.map(p => {
    p.getType match {
      case Some(PortInstance.Type.DefPort(symbol)) =>
        Some((p.getUnqualifiedName, PortCppWriter(s, symbol.node)))
      case _ => None
    }
  }).filter(_.isDefined).map(_.get).toMap

  def write: List[CppDoc.Class.Member] = {
    if hasInputPorts then
      List(
        if hasTypedInputPorts then getGetters(typedInputPorts) else Nil,
        if hasSerialInputPorts then getGetters(serialInputPorts) else Nil,
        getNumGetters,
        getEnum,
        if hasTypedInputPorts then getHandlers(typedInputPorts) else Nil,
        if hasTypedInputPorts then getHandlerBases(typedInputPorts) else Nil,
        if hasSerialInputPorts then getHandlers(serialInputPorts) else Nil,
        if hasSerialInputPorts then getHandlerBases(serialInputPorts) else Nil,
        if hasTypedInputPorts then getCallbacks(typedInputPorts) else Nil,
        if hasSerialInputPorts then getCallbacks(serialInputPorts) else Nil,
        if hasTypedInputPorts then getMemberVariables(typedInputPorts) else Nil,
        if hasSerialInputPorts then getMemberVariables(serialInputPorts) else Nil
      ).flatten
    else Nil
  }

  private def getGetters(ports: List[PortInstance.General]): List[CppDoc.Class.Member] = {
    List(
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
            Some("Get input port at index"),
            inputGetterName(p.getUnqualifiedName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type("NATIVE_INT_TYPE"),
                "portNum",
                Some("The port number")
              )
            ),
            CppDoc.Type(s"${getQualifiedPortTypeName(p, PortInstance.Direction.Input)}*"),
            lines(
              s"""|FW_ASSERT(
                  |  portNum < this->${inputNumGetterName(p.getUnqualifiedName)}(),
                  |  static_cast<FwAssertArgType>(portNum)
                  | );
                  |
                  |return &this->${inputMemberName(p.getUnqualifiedName)}[portNum];
                  |"""
            )
          )
        )
      )
    ).flatten
  }

  private def getEnum: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PROTECTED"),
            CppDocWriter.writeBannerComment(
              "Enumerations for numbers of input ports"
            ),
            Line.blank :: wrapInEnum(
              inputPorts.map(p =>
                line(s"${inputEnumName(p.getUnqualifiedName)} = ${p.getArraySize};")
              )
            )
          ).flatten
        )
      )
    )
  }

  private def getNumGetters: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("PROTECTED"),
              CppDocWriter.writeBannerComment(
                "Getters for numbers of input ports"
              )
            ).flatten
          )
        )
      ),
      inputPorts.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Get the number of ${p.getUnqualifiedName} input ports"),
            inputNumGetterName(p.getUnqualifiedName),
            Nil,
            CppDoc.Type("NATIVE_INT_TYPE"),
            Nil
          )
        )
      )
    ).flatten
  }

  private def getHandlers(ports: List[PortInstance.General]): List[CppDoc.Class.Member] = {
    List(
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
            inputHandlerName(p.getUnqualifiedName),
            CppDoc.Function.Param(
              CppDoc.Type("NATIVE_INT_TYPE"),
              "portNum",
              Some("The port number")
            ) :: getFunctionParams(p),
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.PureVirtual
          )
        )
      )
    ).flatten
  }

  private def getHandlerBases(ports: List[PortInstance.General]): List[CppDoc.Class.Member] = {
    List(
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
            inputHandlerBaseName(p.getUnqualifiedName),
            CppDoc.Function.Param(
              CppDoc.Type("NATIVE_INT_TYPE"),
              "portNum",
              Some("The port number")
            ) :: getFunctionParams(p),
            CppDoc.Type("void"),
            Nil
          )
        )
      )
    ).flatten
  }

  private def getCallbacks(ports: List[PortInstance.General]): List[CppDoc.Class.Member] = {
    val functions =
      ports.map(p =>
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Callback for port ${p.getUnqualifiedName}"),
            inputCallbackName(p.getUnqualifiedName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type("Fw::PassiveComponentBase*"),
                "callComp",
                Some("The component instance")
              ),
              CppDoc.Function.Param(
                CppDoc.Type("NATIVE_INT_TYPE"),
                "portNum",
                Some("The port number")
              )
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

  private def getMemberVariables(ports: List[PortInstance.General]): List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PRIVATE"),
            CppDocWriter.writeBannerComment(
              s"${getTypeString(ports.head).capitalize} input ports"
            ),
            ports.flatMap(p => {
              val typeName = getQualifiedPortTypeName(p, PortInstance.Direction.Input)
              val name = inputMemberName(p.getUnqualifiedName)
              val num = inputEnumName(p.getUnqualifiedName)

              lines(
                s"""|
                    |//! Input port ${p.getUnqualifiedName}
                    |$typeName $name[$num];
                    |"""
              )
            })
          ).flatten
        )
      )
    )
  }

  private def getTypeString(p: PortInstance.General) =
    p.getType.get match {
      case PortInstance.Type.DefPort(_) => "typed"
      case PortInstance.Type.Serial => "serial"
    }

  private def getFunctionParams(p: PortInstance.General) =
    p.getType.get match {
      case PortInstance.Type.DefPort(_) =>
        portWriterMap(p.getUnqualifiedName).functionParams
      case PortInstance.Type.Serial =>
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::SerializeBufferBase&"),
            "buffer",
            Some("The serialization buffer")
          )
        )
    }

}