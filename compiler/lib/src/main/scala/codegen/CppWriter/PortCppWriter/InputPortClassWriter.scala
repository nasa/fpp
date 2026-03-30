package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes the input port class for a port definition */
case class InputPortClassWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends PortCppWriterUtils(s, aNode) {

  def write: CppDoc.Member.Class = classMember(
    Some(s"Input $portName port\n$portAnnotation"),
    inputPortClassName,
    Some("public Fw::InputPortBase"),
    List.concat(
      getPublicTypeMembers,
      getPublicConstructorMembers,
      getPublicFunctionMembers,
      getPrivateFunctionMembers,
      getPrivateVariableMembers
    )
  )

  // Write serializer param names as a comma-separated list
  private def writeSerializerParamNames =
    PortCppWriterUtils.writeParamNamesWithPrefix ("_serializer.m_") (portParams)

  // Write serializer param names appended to a comma-separated list
  private def appendSerializerParamNames =
    commaPrefix(writeSerializerParamNames)

  private def getAddCallCompFunction =
    functionClassMember(
      Some("Register a component"),
      "addCallComp",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("Fw::PassiveComponentBase*"),
          "callComp",
          Some("The containing component")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("CompFuncPtr"),
          "funcPtr",
          Some("The port callback function")
        )
      ),
      CppDoc.Type("void"),
      lines(
        """|FW_ASSERT(callComp != nullptr);
           |FW_ASSERT(funcPtr != nullptr);
           |
           |this->m_comp = callComp;
           |this->m_func = funcPtr;
           |this->m_connObj = callComp;
           |"""
      )
    )

  private def getCompFuncParam(p: PortParamType) = {
    val paramData = p._2.data
    val t = formalParamsCppWriter.getFormalParamType(
      paramData,
      "Fw::StringBase"
    )
    line(s"${t.hppType} ${paramData.name}")
  }

  private def getCompFuncParams =
    addSeparators (",") (
      line("Fw::PassiveComponentBase* callComp") ::
      line("FwIndexType portNum") ::
      portParams.map(getCompFuncParam)
    )

  private def getCompFuncType = linesClassMember(
    List.concat(
      lines(
        s"""|
            |//! The port callback function type
            |typedef $returnType (*CompFuncPtr)("""
      ),
      getCompFuncParams.map(indentIn),
      lines(");")
    )
  )

  private def getInitFunction =
    functionClassMember(
      Some("Initialization function"),
      "init",
      Nil,
      CppDoc.Type("void"),
      lines("Fw::InputPortBase::init();")
    )

  private def getInvokeFunction =
    functionClassMember(
      Some("Invoke a port interface"),
      "invoke",
      portFunctionParams,
      CppDoc.Type(returnType),
      lines(
        s"""|#if FW_PORT_TRACING == 1
            |this->trace();
            |#endif
            |
            |FW_ASSERT(this->m_comp != nullptr);
            |FW_ASSERT(this->m_func != nullptr);
            |
            |return this->m_func(this->m_comp, this->m_portNum$appendParamNames);
            |"""
      )
    )

  private def getInvokeSerialFunction =
    functionClassMember(
      Some(
        """|Invoke the port with serialized arguments
           |\return The serialize status"""
      ),
      "invokeSerial",
      List(linearBufferFunctionParam),
      CppDoc.Type("Fw::SerializeStatus"),
      if hasReturnType
      then writeInvokeSerialBodyNonVoid
      else writeInvokeSerialBodyVoid
    )

  private def getPrivateFunctionMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "private",
      s"Private member functions for $inputPortClassName",
      wrapClassMembersInIfDirective(
        "#if FW_PORT_SERIALIZATION == 1",
        List(getInvokeSerialFunction)
      )
    )

  private def getPrivateVariableMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "private",
      s"Private member variables for $inputPortClassName",
      List(
        linesClassMember(
          lines(
            """|
               |//! The pointer to the port callback function
               |CompFuncPtr m_func;"""
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

  private def getPublicConstructorMembers =
    addAccessTagAndComment(
      "public",
      s"Public constructors for $inputPortClassName",
      List(
        constructorClassMember(
          Some("Constructor"),
          Nil,
          List("Fw::InputPortBase()", "m_func(nullptr)"),
          Nil
        )
      )
    )

  private def getPublicFunctionMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      s"Public member functions for $inputPortClassName",
      List(
        getInitFunction,
        getAddCallCompFunction,
        getInvokeFunction
      ),
    )

  private def getPublicTypeMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      s"Public types for $inputPortClassName",
      List(getCompFuncType),
      CppDoc.Lines.Hpp
    )

  private def writeInvokeSerialBodyNonVoid =
    lines(
      """|// For ports with a return type, invokeSerial is not used
         |(void) _buffer;
         |
         |FW_ASSERT(0);
         |return Fw::FW_SERIALIZE_OK;
         |"""
    )

  private def writeInvokeSerialBodyVoid = {
    List.concat(
      lines(
        """|#if FW_PORT_TRACING == 1
           |this->trace();
           |#endif
           |
           |FW_ASSERT(this->m_comp != nullptr);
           |FW_ASSERT(this->m_func != nullptr);"""
      ),
      if (hasParams)
      then lines(
        s"""|
            |$portSerializerName _serializer;
            |Fw::SerializeStatus _status = _serializer.deserializePortArgs(_buffer);
            |if (_status != Fw::FW_SERIALIZE_OK) {
            |  return _status;
            |}"""
      )
      else lines(
        """|
           |(void) _buffer;"""
      ),
      lines(
        s"""|
            |this->m_func(this->m_comp, this->m_portNum$appendSerializerParamNames);
            |
            |return Fw::FW_SERIALIZE_OK;"""
      )
    )
  }

}
