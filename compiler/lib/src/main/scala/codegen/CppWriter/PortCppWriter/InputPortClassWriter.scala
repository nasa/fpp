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
      getTypeMembers,
      getConstructorMembers,
      getPublicFunctionMembers,
      getPrivateFunctionMembers,
      getVariableMembers
    )
  )

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

  private def getConstructorMembers = 
    addAccessTagAndComment(
      "public",
      s"Constructors for $inputPortClassName",
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

  private def getPrivateFunctionMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "private",
      s"Private member functions for $inputPortClassName",
      wrapClassMembersInIfDirective(
        "#if FW_PORT_SERIALIZATION == 1",
        List(getInvokeSerialFunction)
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
            |return this->m_func(this->m_comp, this->m_portNum${appendParamNames});
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

  private def getTypeMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      s"Types for $inputPortClassName",
      List(getCompFuncType),
      CppDoc.Lines.Hpp
    )

  private def getVariableMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "private",
      s"Member variables for $inputPortClassName",
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
    val bufferUse =
      if hasParams
      then line("Fw::SerializeStatus _status;")
      else line("(void) _buffer;")
    bufferUse ::
    List.concat(
      lines(
        """|
           |#if FW_PORT_TRACING == 1
           |this->trace();
           |#endif
           |
           |FW_ASSERT(this->m_comp != nullptr);
           |FW_ASSERT(this->m_func != nullptr);
           |"""
      ),
      portParams.flatMap(param => {
        val data = param._2.data
        val portName = data.name
        val t = s.a.typeMap(data.typeName.id)
        val tn = typeCppWriter.write(t)
        val varDecl = writeVarDecl(s, tn, portName, t)
        lines(
          s"""|
              |$varDecl
              |_status = _buffer.deserializeTo($portName);
              |if (_status != Fw::FW_SERIALIZE_OK) {
              |  return _status;
              |}
              |"""
        )
      }),
      lines(
        s"""|
            |this->m_func(this->m_comp, this->m_portNum${appendParamNames});
            |
            |return Fw::FW_SERIALIZE_OK;
            |"""
      )
    )
  }

}
