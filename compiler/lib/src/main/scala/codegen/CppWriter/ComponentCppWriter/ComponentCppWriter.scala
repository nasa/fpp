package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out C++ for component definitions */
case class ComponentCppWriter (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val name = s.getName(symbol)

  private val fileName = ComputeCppFiles.FileNames.getComponent(name)

  private val namespaceIdentList = s.getNamespaceIdentList(symbol)

  private val kindStr = data.kind match {
    case Ast.ComponentKind.Active => "Active"
    case Ast.ComponentKind.Passive => "Passive"
    case Ast.ComponentKind.Queued => "Queued"
  }

  private val className = s"${name}ComponentBase"

  private val baseClassName = s"${kindStr}ComponentBase"

  private def writeIncludeDirectives: List[String] = {
    val Right(a) = UsedSymbols.defComponentAnnotatedNode(s.a, aNode)
    s.writeIncludeDirectives(a.usedSymbolSet)
  }

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name component base class",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = CppDoc.Member.Class(
      CppDoc.Class(
        AnnotationCppWriter.asStringOpt(aNode),
        className,
        Some(s"public Fw::${baseClassName}"),
        getClassMembers
      )
    )
    List(
      List(hppIncludes, cppIncludes),
      CppWriter.wrapInNamespaces(namespaceIdentList, List(cls))
    ).flatten
  }

  private def getHppIncludes: CppDoc.Member = {
    val mutexHeader =
      if hasGuardedInputPorts then List("Os/Mutex.hpp")
      else Nil
    val standardHeaders = (
      List(
        "FpConfig.hpp",
        "Fw/Port/InputSerializePort.hpp",
        "Fw/Port/OutputSerializePort.hpp",
        s"Fw/Comp/${baseClassName}.hpp"
      ) ++ mutexHeader
    ).map(CppWriter.headerString)
    val symbolHeaders = writeIncludeDirectives
    val headers = standardHeaders ++ symbolHeaders
    CppWriter.linesMember(addBlankPrefix(headers.sorted.map(line)))
  }

  private def getCppIncludes: CppDoc.Member = {
    val systemHeaders = List(
      "cstdio",
    ).map(CppWriter.systemHeaderString).map(line)
    val userHeaders = List(
      "Fw/Types/Assert.hpp",
      "Fw/Types/String.hpp",
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

  private def getClassMembers: List[CppDoc.Class.Member] = {
    List(
      getFriendClasses,
      getComponentFunctions,
      getDispatchFunction,
      ComponentInputPortInstances(s, aNode).write,
      getMutexMembers,
      getOutputPortMembers
    ).flatten
  }

  private def getFriendClasses: List[CppDoc.Class.Member] = {
    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocWriter.writeBannerComment(
              "Friend classes"
            ),
            lines(
              s"""|
                  |//! Friend class for white-box testing
                  |friend class ${className}Friend;
                  |"""
            )
          ).flatten
        )
      )
    )
  }

  private def getComponentFunctions: List[CppDoc.Class.Member] = {
    val initInstanceParam = List(
      CppDoc.Function.Param(
        CppDoc.Type("NATIVE_INT_TYPE"),
        "instance = 0",
        Some("The instance number")
      )
    )
    val initQueueDepthParam =
      if data.kind != Ast.ComponentKind.Passive then List(
        CppDoc.Function.Param(
          CppDoc.Type("NATIVE_INT_TYPE"),
          "queueDepth",
          Some("The queue depth")
        )
      )
      else Nil
    val initMsgSizeParam =
      if hasSerialAsyncInputPorts then List(
        CppDoc.Function.Param(
          CppDoc.Type("NATIVE_INT_TYPE"),
          "msgSize",
          Some("The message size")
        )
      )
      else Nil

    List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PROTECTED"),
            CppDocWriter.writeBannerComment(
              "Component construction, initialization, and destruction"
            )
          ).flatten
        )
      ),
      CppDoc.Class.Member.Constructor(
        CppDoc.Class.Constructor(
          Some(s"Construct ${className} object"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const char*"),
              "compName = \"\"",
              Some("The component name")
            )
          ),
          Nil,
          Nil
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some(s"Initialize ${className} object"),
          "init",
          initQueueDepthParam ++ initMsgSizeParam ++ initInstanceParam,
          CppDoc.Type("void"),
          Nil
        )
      ),
      CppDoc.Class.Member.Destructor(
        CppDoc.Class.Destructor(
          Some(s"Destroy ${className} object"),
          Nil,
          CppDoc.Class.Destructor.Virtual
        )
      )
    )
  }

  private def getDispatchFunction: List[CppDoc.Class.Member] = {
    if data.kind != Ast.ComponentKind.Active then Nil
    else List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PRIVATE"),
            CppDocWriter.writeBannerComment(
              "Message dispatch functions"
            )
          ).flatten
        )
      ),
      CppDoc.Class.Member.Function(
        CppDoc.Function(
          Some("Called in the message loop to dispatch a message from the queue"),
          "doDispatch",
          Nil,
          CppDoc.Type("MsgDispatchStatus"),
          Nil,
          CppDoc.Function.Virtual
        )
      )
    )
  }

  private def getMutexMembers: List[CppDoc.Class.Member] = {
    if !hasGuardedInputPorts then Nil
    else List(
      CppDoc.Class.Member.Lines(
        CppDoc.Lines(
          List(
            CppDocHppWriter.writeAccessTag("PRIVATE"),
            CppDocWriter.writeBannerComment(
              "Mutexes"
            ),
            lines(
              """|
                 |//! Mutex for guarded ports
                 |Os::Mutex m_guardedPortMutex;
                 |"""
            )
          ).flatten
        )
      )
    )
  }

  private def getOutputPortMembers: List[CppDoc.Class.Member] = {
    if outputPorts.isEmpty then Nil
    else
      List(
        getOutputPortConnectors,
      ).flatten
  }

  private def getOutputPortConnectors: List[CppDoc.Class.Member] = {
    List(
      List(
        CppDoc.Class.Member.Lines(
          CppDoc.Lines(
            List(
              CppDocHppWriter.writeAccessTag("public"),
              CppDocWriter.writeBannerComment("" +
                "Connect typed input ports to typed output ports"
              ),
            ).flatten
          )
        )
      ),
      outputPorts.map(p => {
        CppDoc.Class.Member.Function(
          CppDoc.Function(
            Some(s"Connect port to ${p.getUnqualifiedName}[portNum]"),
            outputConnectorName(p.getUnqualifiedName),
            List(
              CppDoc.Function.Param(
                CppDoc.Type("NATIVE_INT_TYPE"),
                "portNum",
                Some("The port number")
              ),
              CppDoc.Function.Param(
                CppDoc.Type(s"${getQualifiedPortTypeName(p, PortInstance.Direction.Input)}*"),
                "port",
                Some("The input port")
              )
            ),
            CppDoc.Type("void"),
            lines(
              s"""|FW_ASSERT(
                  |  portNum < this->${outputNumGetterName(p.getUnqualifiedName)}(),
                  |  static_cast<FwAssertArgType>(portNum)
                  |);
                  |
                  |this->${outputMemberName(p.getUnqualifiedName)}[portNum].addCallPort(port);
                  |"""
            )
          )
        )
      })
    ).flatten
  }

}
