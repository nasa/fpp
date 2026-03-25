package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes the output port class for a port definition */
case class OutputPortClassWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends PortCppWriterUtils(s, aNode) {

  def write = classMember(
    Some(s"Output $portName port\n$portAnnotation"),
    outputPortClassName,
    Some("public Fw::OutputPortBase"),
    List.concat(
      getFunctionMembers,
      getVariableMembers
    )
  )

  private def getAddCallPortFunction =
    functionClassMember(
      Some("Register an input port"),
      "addCallPort",
      List(
        CppDoc.Function.Param(
          CppDoc.Type(s"$inputPortClassName*"),
          "callPort",
          Some("The input port")
        )
      ),
      CppDoc.Type("void"),
      lines(
        """|FW_ASSERT(callPort != nullptr);
           |
           |this->m_port = callPort;
           |this->m_connObj = callPort;
           |
           |#if FW_PORT_SERIALIZATION == 1
           |this->m_serPort = nullptr;
           |#endif
           |"""
      )
    )

  private def getConstructor =
    constructorClassMember(
      Some("Constructor"),
      Nil,
      List("Fw::OutputPortBase()", "m_port(nullptr)"),
      Nil
    )

  private def getFunctionMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      "Output Port Member functions",
      List(
        getConstructor,
        getInitFunction,
        getAddCallPortFunction,
        getInvokeFunction
      )
    )

  private def getInitFunction =
    functionClassMember(
      Some("Initialization function"),
      "init",
      Nil,
      CppDoc.Type("void"),
      lines("Fw::OutputPortBase::init();")
    )

  private def getInvokeFunction =
    functionClassMember(
      {
        val returnComment =
          if hasReturnType
          then "\n\\return The return value of the port handler"
          else ""
        Some(s"Invoke a port connection$returnComment")
      },
      "invoke",
      portFunctionParams,
      CppDoc.Type(returnType),
      List.concat(
        lines(
          s"""|#if FW_PORT_TRACING == 1
              |this->trace();
              |#endif
              |"""
        ),
        if hasReturnType
        then writeInvokeBodyNonVoid
        else writeInvokeBodyVoid
      ),
      CppDoc.Function.NonSV,
      CppDoc.Function.Const
    )

  private def getVariableMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "private",
      "Member variables",
      List(
        linesClassMember(
          lines(
            s"""|
                |//! The pointer to the input port
                |$inputPortClassName* m_port;"""
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

  private def writeInvokeBodyNonVoid =
    lines(
      s"""|
          |FW_ASSERT(this->m_port != nullptr);
          |return this->m_port->invoke($writeParamNames);
          |"""
    )

  private def writeInvokeBodyVoid =
    List.concat(
      lines(
        s"""|
            |#if FW_PORT_SERIALIZATION
            |FW_ASSERT((this->m_port != nullptr) || (this->m_serPort != nullptr));
            |
            |if (this->m_port != nullptr) {
            |  this->m_port->invoke($writeParamNames);
            |}
            |else {
            |  Fw::SerializeStatus _status;
            |  $portBufferName _buffer;
            |"""
      ),
      portParams.flatMap(param => {
        val paramName = param._2.data.name
        lines(
          s"""|
              |  _status = _buffer.serializeFrom($paramName);
              |  FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));
              |"""
        )
      }),
      lines(
        s"""|
            |  _status = this->m_serPort->invokeSerial(_buffer);
            |  FW_ASSERT(_status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_status));
            |}
            |#else
            |FW_ASSERT(this->m_port != nullptr);
            |this->m_port->invoke($writeParamNames);
            |#endif
            |"""
      )
    )

}
