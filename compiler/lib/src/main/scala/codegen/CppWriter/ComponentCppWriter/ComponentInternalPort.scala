package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component internal ports */
case class ComponentInternalPort (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      getHandlers,
      getHandlerBases
    ).flatten
  }

  private def getHandlers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      "Internal interface handlers",
      internalPorts.map(p =>
        functionClassMember(
          Some(
            s"Internal interface handler for ${p.getUnqualifiedName}"
          ),
          internalInterfaceHandlerName(p.getUnqualifiedName),
          getPortFunctionParams(p),
          CppDoc.Type("void"),
          Nil,
          CppDoc.Function.PureVirtual
        )
      ),
      CppDoc.Lines.Hpp
    )
  }

  private def getHandlerBases: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      "Internal interface base-class functions",
      internalPorts.map(p =>
        functionClassMember(
          Some(
            s"Internal interface base-class function for ${p.getUnqualifiedName}"
          ),
          internalInterfaceHandlerBaseName(p.getUnqualifiedName),
          getPortFunctionParams(p),
          CppDoc.Type("void"),
          intersperseBlankLines(
            List(
              lines(
                s"""|ComponentIpcSerializableBuffer msg;
                    |Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
                    |
                    |// Serialize the message ID
                    |_status = msg.serialize(static_cast<NATIVE_INT_TYPE>(${internalPortCppConstantName(p)}));
                    |FW_ASSERT (
                    |  _status == Fw::FW_SERIALIZE_OK,
                    |  static_cast<FwAssertArgType>(_status)
                    |);
                    |
                    |// Fake port number to make message dequeue work
                    |_status = msg.serialize(static_cast<NATIVE_INT_TYPE>(0));
                    |FW_ASSERT (
                    |  _status == Fw::FW_SERIALIZE_OK,
                    |  static_cast<FwAssertArgType>(_status)
                    |);
                    |"""
              ),
              intersperseBlankLines(
                getPortParams(p).map((n, _) =>
                  lines(
                    s"""|_status = msg.serialize($n);
                        |FW_ASSERT(
                        |  _status == Fw::FW_SERIALIZE_OK,
                        |  static_cast<FwAssertArgType>(_status)
                        |);
                        |""".stripMargin
                  )
                )
              ),
              writeSendMessageLogic("msg", p.queueFull, p.priority)
            )
          )
        )
      )
    )
  }

  // Get the name for an internal interface base-class function
  private def internalInterfaceHandlerBaseName(name: String) =
    s"${name}_internalInterfaceInvoke"

}