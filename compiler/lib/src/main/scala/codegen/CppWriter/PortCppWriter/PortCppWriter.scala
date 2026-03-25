package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes C++ port definitions */
case class PortCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends PortCppWriterUtils(s, aNode) {

  private val portBufferClass = PortBufferClass(s, aNode)

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(portSymbol, portFileName)
    CppWriter.createCppDoc(
      s"$portName port",
      portFileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getClasses =
    List.concat(
      guardedList (!hasReturnValue) (List(portBufferClass.get)),
      wrapMembersInIfDirective(
        "#if !FW_DIRECT_PORT_CALLS",
        List(
          InputPortClass.get,
          OutputPortClass.get
        ),
        CppDoc.Lines.Both
      )
    )

  private def getMembers: List[CppDoc.Member] =
    List.concat(
      List(
        getHppIncludes,
        getCppIncludes
      ),
      wrapInNamespaces(
        namespaceIdentList,
        getClasses
      )
    )

  private def getHppIncludes: CppDoc.Member = {
    val unconditional = List.concat(
      List(
        "Fw/FPrimeBasicTypes.hpp",
        "Fw/Types/String.hpp"
      ).map(CppWriter.headerString),
      writeIncludeDirectives
    ).sorted.map(line)
    val conditional = List.concat(
      guardedList (!hasReturnValue) (List("Fw/Types/Serializable.hpp")),
      List(
        "Fw/Comp/PassiveComponentBase.hpp",
        "Fw/Port/InputPortBase.hpp",
        "Fw/Port/OutputPortBase.hpp",
      )
    ).map(CppWriter.headerString).sorted.map(line)
    linesMember(
      Line.blank ::
      List.concat(
        unconditional,
        lines("#if !FW_DIRECT_PORT_CALLS"),
        conditional,
        lines("#endif")
      )
    )
  }

  private def getCppIncludes: CppDoc.Member = {
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/ExternalString.hpp",
      s.getIncludePath(portSymbol, portFileName)
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(
      Line.blank :: userHeaders,
      CppDoc.Lines.Cpp
    )
  }

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defPortAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  /** Object for constructing the input port class */
  private object InputPortClass {

    def get = classMember(
      Some(s"Input $portName port\n$portAnnotation"),
      PortCppWriter.inputPortName(portName),
      Some("public Fw::InputPortBase"),
      List.concat(
        getTypeMembers,
        getFunctionMembers,
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

    private def getCompFuncParam(p: PortCppWriter.PortParamType) = {
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

    private def getConstructor = 
      constructorClassMember(
        Some("Constructor"),
        Nil,
        List("Fw::InputPortBase()", "m_func(nullptr)"),
        Nil
      )

    private def getFunctionMembers: List[CppDoc.Class.Member] =
      addAccessTagAndComment(
        "public",
        "Input Port Member functions",
        List.concat(
          List(
            getConstructor,
            getInitFunction,
            getAddCallCompFunction,
            getInvokeFunction,
            linesClassMember(CppDocHppWriter.writeAccessTag("private")),
          ),
          wrapClassMembersInIfDirective(
            "#if FW_PORT_SERIALIZATION == 1",
            List(getInvokeSerialFunction)
          )
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
        List(bufferFunctionParam),
        CppDoc.Type("Fw::SerializeStatus"),
        if hasReturnValue
        then writeInvokeSerialBodyNonVoid
        else writeInvokeSerialBodyVoid
      )

    private def getTypeMembers: List[CppDoc.Class.Member] =
      addAccessTagAndComment(
        "public",
        "Types",
        List(getCompFuncType),
        CppDoc.Lines.Hpp
      )

    private def getVariableMembers: List[CppDoc.Class.Member] =
      addAccessTagAndComment(
        "private",
        "Member variables",
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

  /** Object for constructing the output port class */
  private object OutputPortClass {

    def get = classMember(
      Some(s"Output $portName port\n$portAnnotation"),
      PortCppWriter.outputPortName(portName),
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
            CppDoc.Type(s"${PortCppWriter.inputPortName(portName)}*"),
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
            if hasReturnValue
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
          if hasReturnValue
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
                  |${PortCppWriter.inputPortName(portName)}* m_port;"""
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

}

object PortCppWriter {

  type PortParamType = Ast.Annotated[AstNode[Ast.FormalParam]]

  def inputPortName(name: String) = s"Input${name}Port"

  def outputPortName(name: String) = s"Output${name}Port"

  /** Gets the name of the port buffer class */
  def getPortBufferName(name: String) = s"${name}PortBuffer"

  /** Get the name of a port type */
  def getPortName(name: String, direction: PortInstance.Direction): String =
    direction match {
      case PortInstance.Direction.Input => inputPortName(name)
      case PortInstance.Direction.Output => outputPortName(name)
    }

}
