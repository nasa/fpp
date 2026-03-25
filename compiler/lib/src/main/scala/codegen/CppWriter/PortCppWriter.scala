package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

case class PortCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends CppWriterUtils {

  private val node = aNode._2

  private val annotation = AnnotationCppWriter.asString(aNode)

  private val data = node.data

  private val symbol = Symbol.Port(aNode)

  private val portName = s.getName(symbol)

  private val portBufferName = PortCppWriter.getPortBufferName(portName)

  private val fileName = ComputeCppFiles.FileNames.getPort(portName)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s, "Fw::StringBase")

  private val returnTypeCppWriter = TypeCppWriter(s, "Fw::String")

  private val formalParamsCppWriter = FormalParamsCppWriter(s)

  private val params = data.params

  private val bufferFunctionParam = CppDoc.Function.Param(
    CppDoc.Type("Fw::LinearBufferBase&"),
    "_buffer",
    Some("The serial buffer")
  )

  // Param names in a comma-separated list
  def writeParamNames = params.map(_._2.data.name).mkString(", ")

  // Param names appended to a comma-separated list
  val appendParamNames = writeParamNames match {
    case "" => ""
    case paramNames => s", $paramNames"
  }

  // Port params as CppDoc Function Params
  private val portFunctionParams: List[CppDoc.Function.Param] =
    formalParamsCppWriter.write(params, "Fw::StringBase")

  // Return type as a C++ type
  private val returnType = data.returnType match {
    case Some(value) => returnTypeCppWriter.write(s.a.typeMap(value.id))
    case None => "void"
  }

  private val hasReturnType = data.returnType.isDefined

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defPortAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$portName port",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getClasses =
    List.concat(
      guardedList (!hasReturnType) (List(PortBufferClass.get)),
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
      guardedList (!hasReturnType) (List("Fw/Types/Serializable.hpp")),
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
      s.getIncludePath(symbol, fileName)
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(
      Line.blank :: userHeaders,
      CppDoc.Lines.Cpp
    )
  }

  private def writeBufferCapacity: List[Line] = writeSum(
    params.map(
      param => {
        val data = param._2.data
        val t = s.a.typeMap(data.typeName.id)
        val tn = typeCppWriter.write(t)
        writeStaticSerializedSizeExpr(s, t, tn)
      }
    )
  )

  /** Object for constructing the port buffer class */
  private object PortBufferClass {

    def get = classMember(
      Some(s"Serialization buffer for $portName port\n$annotation"),
      portBufferName,
      Some("public Fw::LinearBufferBase"),
      List.concat(
        getPublicConstants,
        getPublicMemberFunctions,
        getPublicStaticFunctions,
        getPrivateMemberVariables
      )
    )

    private def getPublicConstants = addAccessTagAndComment(
      "public",
      "Public constants",
      List(
        linesClassMember(
          List.concat(
            lines(
              s"""|
                  |//! The buffer capacity. This is the sum of the static serialized
                  |//! sizes of the port arguments.
                  |static constexpr FwSizeType CAPACITY ="""
            ),
            writeBufferCapacity.map(indentIn)
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

    private def getPublicMemberFunctions = addAccessTagAndComment(
      "public",
      "Public member functions",
      List(
        linesClassMember({
          val buffAddr =
            if params.isEmpty then "nullptr" else "m_buff"
          lines(
            s"""|
                |//! Get the capacity of the buffer
                |//! \\return The capacity
                |Fw::Serializable::SizeType getCapacity() const override {
                |  return CAPACITY;
                |}
                |
                |//! Get the buffer address (non-const)
                |//! \\return The buffer address
                |U8* getBuffAddr() override {
                |  return $buffAddr;
                |}
                |
                |//! Get the buffer address (const)
                |//! \\return The buffer address
                |const U8* getBuffAddr() const override {
                |  return $buffAddr;
                |}
                |"""
          )
        })
      ),
      CppDoc.Lines.Hpp
    )

    private def getPublicStaticFunctions =
      addAccessTagAndComment(
        "public",
        "Public static functions",
        guardedList (!params.isEmpty) (
          List(getSerializeFunction)
        )
      )

    private def writeSerializationForParam(param: PortCppWriter.PortParamType) = {
      val paramName = param._2.data.name
      lines(
        s"""|if (_status == Fw::FW_SERIALIZE_OK) {
            |  _status = _buffer.serializeFrom($paramName);
            |}"""
      )
    }

    private def getSerializeFunction =
      functionClassMember(
        Some("Serialize port arguments into the buffer"),
        "serializePortArgs",
        portFunctionParams :+ bufferFunctionParam,
        CppDoc.Type("Fw::SerializeStatus"),
        List.concat(
          lines("Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;"),
          params.flatMap(writeSerializationForParam),
          lines("return _status;")
        ),
        CppDoc.Function.Static
      )

    private def getPrivateMemberVariables =
      addAccessTagAndComment(
        "private",
        "Private member variables",
        guardedList (!params.isEmpty) (
          List(
            linesClassMember(
              Line.blank ::
              lines(s"U8 m_buff[CAPACITY];")
            )
          )
        ),
        CppDoc.Lines.Hpp
      )

  }

  /** Object for constructing the input port class */
  private object InputPortClass {

    def get = classMember(
      Some(s"Input $portName port\n$annotation"),
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
        params.map(getCompFuncParam)
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
        Some("Invoke the port with serialized arguments"),
        "invokeSerial",
        List(bufferFunctionParam),
        CppDoc.Type("Fw::SerializeStatus"),
        if hasReturnType
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
        if params.isEmpty
        then line("(void) _buffer;")
        else line("Fw::SerializeStatus _status;")
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
        params.flatMap(param => {
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
      Some(s"Output $portName port\n$annotation"),
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
        Some("Invoke a port interface"),
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
        params.flatMap(param => {
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
