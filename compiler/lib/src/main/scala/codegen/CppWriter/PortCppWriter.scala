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

  private val fileName = ComputeCppFiles.FileNames.getPort(portName)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s, "Fw::StringBase")

  private val returnTypeCppWriter = TypeCppWriter(s, "Fw::String")

  private val formalParamsCppWriter = FormalParamsCppWriter(s)

  private val params = data.params

  // Map from param name to param type
  private val paramTypeMap = params.map((_, node, _) => {
    (node.data.name, s.a.typeMap(node.data.typeName.id))
  }).toMap

  // List of tuples (name, type) for each string param
  private val strParamList = paramTypeMap.map((n, t) => t.getUnderlyingType match {
    case t: Type.String => Some((n, t))
    case _ => None
  }).filter(_.isDefined).map(_.get).toList

  // Map from string size to list of names of string of that size
  private val strNameMap = strParamList.groupBy((_, t) => {
    writeStringSize(s, t)
  }).map((size, l) => (size, l.map(_._1)))

  // List of tuples (name, C++ type, kind) for each param
  private val paramList = params.map((_, node, _) => {
    val n = node.data.name
    val k = node.data.kind
    val t = paramTypeMap(n)

    (n, typeCppWriter.write(t), k)
  })

  // Param names in a comma-separated list
  val paramNames = paramList.map(_._1).mkString(", ")

  // Param names appended to a comma-separated list
  val appendedParamNames = paramNames match {
    case "" => ""
    case _ => s", $paramNames"
  }

  // Port params as CppDoc Function Params
  private val functionParams: List[CppDoc.Function.Param] =
    formalParamsCppWriter.write(params, "Fw::StringBase")

  // Return type as a C++ type
  private val returnType = data.returnType match {
    case Some(value) => returnTypeCppWriter.write(s.a.typeMap(value.id))
    case None => "void"
  }

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
      guardedList (!data.returnType.isDefined) (List(PortBufferClass.get)),
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
        List.concat(
          getPortConstants,
          getClasses,
        )
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
      guardedList (!data.returnType.isDefined) (List("Fw/Types/Serializable.hpp")),
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

  private def writeSerializedSize(
    paramList: List[(String, String, Ast.FormalParam.Kind)],
  ): List[Line] = writeSum(
    paramList.map(
      (n, tn, _) => writeStaticSerializedSizeExpr(s, paramTypeMap(n), tn)
    )
  )

  private def getPortConstants: List[CppDoc.Member] = List(
    linesMember(
      Line.blank ::
      line(s"//! $portName port constants") ::
      wrapInNamedStruct(
        PortCppWriter.getPortConstantsName(portName),
        line("//! The size of the serial representations of the port arguments") ::
        line(s"static constexpr FwSizeType INPUT_SERIALIZED_SIZE =") ::
        writeSerializedSize(paramList).map(indentIn)
      )
    )
  )

  /** Object for constructing the port buffer class */
  private object PortBufferClass {

    def get = linesMember(
      List.concat(
        CppDocWriter.writeDoxygenComment(s"$portName buffer\n$annotation"),
        lines(s"class ${portName}PortBuffer : public Fw::LinearBufferBase {"),
        List.concat(
          writeConstants,
          writeMemberFunctions,
          writeMemberVariables
        ).map(indentIn).map(indentIn),
        Line.blank :: lines("};"),
      )
    )

    private def writeConstants =
      List.concat(
        CppDocHppWriter.writeAccessTag("public"),
        Line.blank ::
        line(s"static constexpr FwSizeType SERIALIZED_SIZE =") ::
        writeSerializedSize(paramList).map(indentIn)
      )

    private def writeMemberFunctions = {
      val buffAddr =
        if params.isEmpty then "nullptr" else "m_buff"
      List.concat(
        CppDocHppWriter.writeAccessTag("public"),
        Line.blank ::
        lines(
          s"""|Fw::Serializable::SizeType getCapacity() const {
              |  return SERIALIZED_SIZE;
              |}
              |
              |U8* getBuffAddr() {
              |  return $buffAddr;
              |}
              |
              |const U8* getBuffAddr() const {
              |  return $buffAddr;
              |}
              |"""
        )
      )
    }

    def writeMemberVariables =
      guardedList (!params.isEmpty) (
        List.concat(
          CppDocHppWriter.writeAccessTag("private"),
          List(Line.blank),
          lines(s"U8 m_buff[SERIALIZED_SIZE];"
        )
      )
    )

  }

  /** Object for constructing the input port class */
  private object InputPortClass {

    def get = classMember(
      Some(s"Input $portName port\n$annotation"),
      PortCppWriter.inputPortName(portName),
      Some("public Fw::InputPortBase"),
      List.concat(
        getConstantMembers,
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

    private def getCompFuncParam(p: Ast.Annotated[AstNode[Ast.FormalParam]]) = {
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

    private def getConstantMembers: List[CppDoc.Class.Member] =
      addAccessTagAndComment(
        "public",
        "Constants",
        List(getSerializedSizeConstant),
        CppDoc.Lines.Hpp
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
        functionParams,
        CppDoc.Type(returnType),
        lines(
          s"""|#if FW_PORT_TRACING == 1
              |this->trace();
              |#endif
              |
              |FW_ASSERT(this->m_comp != nullptr);
              |FW_ASSERT(this->m_func != nullptr);
              |
              |return this->m_func(this->m_comp, this->m_portNum${appendedParamNames});
              |"""
        )
      )

    private def getInvokeSerialFunction =
      functionClassMember(
        Some("Invoke the port with serialized arguments"),
        "invokeSerial",
        List(
          CppDoc.Function.Param(
            CppDoc.Type("Fw::LinearBufferBase&"),
            "_buffer"
          )
        ),
        CppDoc.Type("Fw::SerializeStatus"),
        data.returnType match {
          case Some(_) => writeInvokeSerialBodyNonVoid
          case None => writeInvokeSerialBodyVoid
        }
      )

    private def getSerializedSizeConstant = linesClassMember(
      addBlankPrefix(
        wrapInEnum(
          lines({
            val constantsName = PortCppWriter.getPortConstantsName(portName)
            s"""|//! The size of the serial representations of the port arguments
                |SERIALIZED_SIZE = $constantsName::INPUT_SERIALIZED_SIZE"""
          })
        )
      )
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
        paramList.flatMap((n, tn, _) => {
          val varDecl = writeVarDecl(s, tn, n, paramTypeMap(n))
          lines(
            s"""|
                |$varDecl
                |_status = _buffer.deserializeTo($n);
                |if (_status != Fw::FW_SERIALIZE_OK) {
                |  return _status;
                |}
                |"""
          )
        }),
        lines(
          s"""|
              |this->m_func(this->m_comp, this->m_portNum${appendedParamNames});
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
        functionParams,
        CppDoc.Type(returnType),
        List.concat(
          lines(
            s"""|#if FW_PORT_TRACING == 1
                |this->trace();
                |#endif
                |"""
          ),
          data.returnType match {
            case Some(_) => writeInvokeBodyNonVoid
            case None => writeInvokeBodyVoid
          }
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
            |return this->m_port->invoke($paramNames);
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
              |  this->m_port->invoke($paramNames);
              |}
              |else {
              |  Fw::SerializeStatus _status;
              |  ${portName}PortBuffer _buffer;
              |"""
        ),
        paramList.flatMap((n, _, _) => {
          lines(
            s"""|
                |  _status = _buffer.serializeFrom($n);
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
              |this->m_port->invoke($paramNames);
              |#endif
              |"""
        )
      )

  }

}

object PortCppWriter {

  private def inputPortName(name: String) = s"Input${name}Port"

  private def outputPortName(name: String) = s"Output${name}Port"

  /** Gets the name of the port constants struct */
  def getPortConstantsName(name: String) = s"${name}PortConstants"

  /** Get the name of a port type */
  def getPortName(name: String, direction: PortInstance.Direction): String =
    direction match {
      case PortInstance.Direction.Input => inputPortName(name)
      case PortInstance.Direction.Output => outputPortName(name)
    }

  /** Get the name of the port string class namespace */
  def getPortStringNamespace(s: CppWriterState, symbol: Symbol.Port): String = {
    val fppName = s.a.getQualifiedName(symbol)
    val cppName = CppWriter.writeQualifiedName(fppName)
    s"${cppName}PortStrings"
  }

  /** Get the names of port namespaces as a list */
  def getPortNamespaces(s: CppWriterState, symbol: Symbol.Port): List[String] =
    List(getPortStringNamespace(s, symbol))

}
