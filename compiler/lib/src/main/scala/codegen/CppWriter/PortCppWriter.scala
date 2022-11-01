package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

case class PortCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefPort]]
) extends CppWriterLineUtils {

  private val node = aNode._2

  private val data = node.data

  private val symbol = Symbol.Port(aNode)

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getPort(name)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val typeCppWriter = TypeCppWriter(s)

  private val strCppWriter = StringCppWriter(s, Some(name))

  private val params = data.params

  // Map from param name to param type
  private val paramTypeMap = params.map((_, node, _) => {
    (node.data.name, s.a.typeMap(node.data.typeName.id))
  }).toMap

  // Map from param name to param annotation
  private val paramAnnotationMap = params.map(aNode => {
    (aNode._2.data.name, AnnotationCppWriter.asStringOpt(aNode))
  }).toMap

  // List of each param name, C++ type, and kind
  private val paramList = params.map((_, node, _) => {
    val n = node.data.name
    val k = node.data.kind
    paramTypeMap(n) match {
      case t: Type.String => (n, strCppWriter.getClassName(t), k)
      case t => (n, typeCppWriter.write(t), k)
    }
  })

  // Port params as CppDoc Function Params
  private val functionParams = paramList.map((n, tn, k) => {
    CppDoc.Function.Param(
      CppDoc.Type(
        (paramTypeMap(n), k)  match {
          case (_, Ast.FormalParam.Ref) => s"$tn&"
          case (t, Ast.FormalParam.Value) if s.isPrimitive(t, tn) => tn
          case (_, Ast.FormalParam.Value) => s"const $tn&"
        }
      ),
      n,
      paramAnnotationMap(n)
    )
  })

  // Return type as a C++ type
  private val returnType = data.returnType match {
    case Some(value) => s.a.typeMap(value.id) match {
      case t: Type.String => strCppWriter.getClassName(t)
      case t => typeCppWriter.write(t)
    }
    case None => "void"
  }

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defPortAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name port",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val annotation = AnnotationCppWriter.asStringOpt(aNode) match {
      case Some(value) => s"\n$value"
      case None => ""
    }
    val classes = List(
      getStringClasses,
      List(
        CppDoc.Member.Class(
          CppDoc.Class(
            Some(s"Input $name port" + annotation),
            s"Input${name}Port",
            Some("public Fw::InputPortBase"),
            getInputPortClassMembers
          )
        ),
        CppDoc.Member.Class(
          CppDoc.Class(
            Some(s"Output $name port" + annotation),
            s"Output${name}Port",
            Some("public Fw::OutputPortBase"),
            getOutputPortClassMembers
          )
        ),
      )
    ).flatten
    List(
      List(hppIncludes, cppIncludes),
      CppWriter.wrapInNamespaces(namespaceIdentList, classes)
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val standardHeaders = List(
      "FpConfig.hpp",
      "Fw/Cmd/CmdArgBuffer.hpp",
      "Fw/Comp/PassiveComponentBase.hpp",
      "Fw/Port/InputPortBase.hpp",
      "Fw/Port/OutputPortBase.hpp",
      "Fw/Types/Serializable.hpp",
      "Fw/Types/StringType.hpp",
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = standardHeaders ++ symbolHeaders
    CppWriter.linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemHeaders = List(
      "cstdio",
      "cstring",
    ).map(CppWriter.systemHeaderString).map(line)
    val userHeaders = List(
      "Fw/Types/StringUtils.hpp",
      s"${s.getRelativePath(fileName).toString}.hpp"
    ).sorted.map(CppWriter.headerString).map(line)
    CppWriter.linesMember(
      List(
        Line.blank :: systemHeaders,
        Line.blank :: userHeaders
      ).flatten,
      CppDoc.Lines.Cpp
    )
  }

  private def getStringClasses: List[CppDoc.Member] = {
    val strTypes = paramTypeMap.map((_, t) => t match {
      case t: Type.String => Some(t)
      case _ => None
    }).filter(_.isDefined).map(_.get).toList
    strTypes match {
      case Nil => Nil
      case l => strCppWriter.write(l)
    }
  }

  private def getInputPortClassMembers: List[CppDoc.Class.Member] = {
    List(
      getInputPortConstantMembers,
      getInputPortTypeMembers,
      getInputPortFunctionMembers,
      getInputPortVariableMembers
    ).flatten
  }

  private def getInputPortConstantMembers: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public") ++
            CppDocWriter.writeBannerComment("Constants") ++
            addBlankPrefix(
              wrapInEnum(
                List(
                  lines("//! The size of the serial representations of the port arguments"),
                  if params.isEmpty then
                    lines("SERIALIZED_SIZE = 0")
                  else
                    line("SERIALIZED_SIZE =") ::
                      lines(paramList.map((n, tn, _) =>
                        s.getSerializedSizeExpr(paramTypeMap(n), tn)
                      ).mkString(" +\n")).map(indentIn)
                ).flatten
              )
            )
        )
      )
    )
  }

  private def getInputPortTypeMembers: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("public"),
            CppDocWriter.writeBannerComment("Types"),
            Line.blank :: lines("//! The port callback function type"),
            lines(s"typedef $returnType (*CompFuncPtr)("),
            lines("Fw::PassiveComponentBase* callComp,").map(indentIn),
            if params.isEmpty then
              lines("NATIVE_INT_TYPE portNum").map(indentIn)
            else
              indentIn(line("NATIVE_INT_TYPE portNum,")) ::
                writeCppParams.map(indentIn),
            lines(");")
          ).flatten
        )
      )
    )
  }

  private def getInputPortFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public")
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Input Port Member functions"),
          CppDoc.Lines.Both
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Constructor"),
          Nil,
          List("Fw::InputPortBase()", "m_func(nullptr)"),
          Nil
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Initialization function"),
          "init",
          Nil,
          CppDoc.Type("void"),
          lines("Fw::InputPortBase::init();")
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
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
            s"""|FW_ASSERT(callComp);
                |FW_ASSERT(funcPtr);
                |
                |this->m_comp = callComp;
                |this->m_func = funcPtr;
                |this->m_connObj = callComp;"""
          )
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Invoke a port interface"),
          "invoke",
          functionParams,
          CppDoc.Type("U32"),
          lines(
            s"""|#if FW_PORT_TRACING == 1
                |this->trace();
                |#endif
                |
                |FW_ASSERT(this->m_comp);
                |FW_ASSERT(this->m_func);
                |
                |return this->m_func(this->m_comp, this->portNum${if params.isEmpty then ""
                  else ", " + paramList.map(_._1).mkString(", ")});"""
          )
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("private")
        )
      ),
    ) ++
      wrapClassMembersInIfDirective(
        "\n#if FW_PORT_SERIALIZATION == 1",
        List(
          CppDoc.Class.Member.Function(
            CppDoc.Function(
              Some("Invoke the port with serialized arguments"),
              "invokeSerial",
              List(
                CppDoc.Function.Param(
                  CppDoc.Type("Fw::SerializeBufferBase&"),
                  "buffer"
                )
              ),
              CppDoc.Type("Fw::SerializeStatus"),
              lines(
                s"""|FW_ASSERT(0);
                    |
                    |return Fw::FW_SERIALIZE_OK;"""
              )
            )
          )
        ),
      )
  }

  private def getInputPortVariableMembers: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("private"),
            CppDocWriter.writeBannerComment("Member variables"),
            Line.blank :: lines("//! The pointer to the port callback function"),
            lines("CompFuncPtr m_func;")
          ).flatten
        )
      )
    )
  }

  private def getOutputPortClassMembers: List[CppDoc.Class.Member] = {
    List(
      getOutputPortFunctionMembers,
      getOutputPortVariableMembers
    ).flatten
  }

  private def getOutputPortFunctionMembers: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocHppWriter.writeAccessTag("public")
        )
      ),
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          CppDocWriter.writeBannerComment("Output Port Member functions"),
          CppDoc.Lines.Both
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some("Constructor"),
          Nil,
          List("Fw::OutputPortBase()", "m_port(nullptr)"),
          Nil
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Initialization function"),
          "init",
          Nil,
          CppDoc.Type("void"),
          lines("Fw::OutputPortBase::init();")
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Register an input port"),
          "addCallPort",
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"Input${name}Port*"),
              "callPort",
              Some("The input port")
            )
          ),
          CppDoc.Type("void"),
          lines(
            s"""|FW_ASSERT(callComp);
                |
                |this->m_port = callPort;
                |this->m_connObj = callPort;
                |
                |#if FW_PORT_SERIALIZATION == 1
                |this->m_serPort = nullptr;
                |#endif"""
          )
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Invoke a port interface"),
          "invoke",
          functionParams,
          CppDoc.Type("U32"),
          lines(
            s"""|#if FW_PORT_TRACING == 1
                |this->trace();
                |#endif
                |
                |#if FW_PORT_SERIALIZATION
                |FW_ASSERT(this->m_port || this->m_serPort);
                |#else
                |FW_ASSERT(this->m_port);
                |#endif
                |
                |return this->m_port->invoke(${paramList.map(_._1).mkString(", ")});"""
          )
        )
      )
    )
  }

  private def getOutputPortVariableMembers: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("private"),
            CppDocWriter.writeBannerComment("Member variables"),
            Line.blank :: lines("//! The pointer to the input port"),
            lines(s"Input${name}Port* m_port;")
          ).flatten
        )
      )
    )
  }

  // Write params as C++ function arguments
  private def writeCppParams: List[Line] = {
    lines(paramList.map((n, tn, k) => {
      (paramTypeMap(n), k) match {
        case (_, Ast.FormalParam.Ref) => s"$tn& $n"
        case (t, Ast.FormalParam.Value) if s.isPrimitive(t, tn) => s"$tn $n"
        case (_, Ast.FormalParam.Value) => s"const $tn& $n"
      }
    }).mkString(",\n"))
  }

}
