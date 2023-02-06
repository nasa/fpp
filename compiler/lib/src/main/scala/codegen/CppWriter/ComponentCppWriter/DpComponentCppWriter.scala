package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out C++ for component data product definitions */
case class DpComponentCppWriter (
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

  private val className = s"${name}DpComponentBase"

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
      // TODO
      Nil, //getMembers,
      s.toolName
    )
  }

//  private def getMembers: List[CppDoc.Member] = {
//    val hppIncludes = getHppIncludes
//    val cppIncludes = getCppIncludes
//    val cls = classMember(
//      Some(
//        addSeparatedString(
//          s"\\class $className\n\\brief Auto-generated base for $name component",
//          AnnotationCppWriter.asStringOpt(aNode)
//        )
//      ),
//      className,
//      Some(s"public Fw::$baseClassName"),
//      getClassMembers
//    )
//    List(
//      List(hppIncludes, cppIncludes),
//      wrapInNamespaces(namespaceIdentList, List(cls))
//    ).flatten
//  }
//
//  private def getHppIncludes: CppDoc.Member = {
//    // Conditional headers
//    val mutexHeader =
//      if hasGuardedInputPorts || hasParameters then List("Os/Mutex.hpp")
//      else Nil
//    val cmdStrHeader =
//      if hasCommands || hasParameters then List("Fw/Cmd/CmdString.hpp")
//      else Nil
//    val tlmStrHeader =
//      if hasChannels then List("Fw/Tlm/TlmString.hpp")
//      else Nil
//    val prmStrHeader =
//      if hasParameters then List("Fw/Prm/PrmString.hpp")
//      else Nil
//    val logStrHeader =
//      if hasEvents then List("Fw/Log/LogString.hpp")
//      else Nil
//    val internalStrHeader =
//      if hasInternalPorts then List("Fw/Types/InternalInterfaceString.hpp")
//      else Nil
//
//    val standardHeaders = List(
//      List(
//        "FpConfig.hpp",
//        "Fw/Port/InputSerializePort.hpp",
//        "Fw/Port/OutputSerializePort.hpp",
//        "Fw/Comp/ActiveComponentBase.hpp"
//      ),
//      mutexHeader,
//      cmdStrHeader,
//      tlmStrHeader,
//      prmStrHeader,
//      logStrHeader,
//      internalStrHeader
//    ).flatten.map(CppWriter.headerString)
//    val symbolHeaders = writeIncludeDirectives
//    val headers = standardHeaders ++ symbolHeaders
//    linesMember(addBlankPrefix(headers.sorted.flatMap({
//      case s: "#include \"Fw/Log/LogTextPortAc.hpp\"" =>
//        lines(
//          s"""|#if FW_ENABLE_TEXT_LOGGING == 1
//              |$s
//              |#endif
//              |""".stripMargin
//        )
//      case s => lines(s)
//    })))
//  }
//
//  private def getCppIncludes: CppDoc.Member = {
//    val systemHeaders = List(
//      "cstdio",
//    ).map(CppWriter.systemHeaderString).map(line)
//    val userHeaders = List(
//      "Fw/Types/Assert.hpp",
//      "Fw/Types/String.hpp",
//      s"${s.getRelativePath(fileName).toString}.hpp"
//    ).sorted.map(CppWriter.headerString).map(line)
//    linesMember(
//      List(
//        Line.blank :: systemHeaders,
//        Line.blank :: userHeaders
//      ).flatten,
//      CppDoc.Lines.Cpp
//    )
//  }
//
//  private def getClassMembers: List[CppDoc.Class.Member] = {
//    List(
//      // Friend classes
//      getFriendClassMembers,
//
//      // Constants
//      getConstantMembers,
//
//      // Public function members
//      portWriter.getPublicFunctionMembers,
//      cmdWriter.getPublicFunctionMembers,
//      paramWriter.getPublicFunctionMembers,
//
//      // Protected function members
//      getComponentFunctionMembers,
//      portWriter.getProtectedFunctionMembers,
//      internalPortWriter.getFunctionMembers,
//      cmdWriter.getProtectedFunctionMembers,
//      eventWriter.getFunctionMembers,
//      tlmWriter.getFunctionMembers,
//      paramWriter.getProtectedFunctionMembers,
//      getTimeFunctionMember,
//      getMutexOperationMembers,
//
//      // Protected/private function members
//      getDispatchFunctionMember,
//
//      // Private function members
//      portWriter.getPrivateFunctionMembers,
//      paramWriter.getPrivateFunctionMembers,
//
//      // Member variables
//      portWriter.getVariableMembers,
//      eventWriter.getVariableMembers,
//      tlmWriter.getVariableMembers,
//      paramWriter.getVariableMembers,
//      getMsgSizeVariableMember,
//      getMutexVariableMembers,
//    ).flatten
//  }
//
//  private def getConstantMembers: List[CppDoc.Class.Member] = {
//    val constants = List(
//      portWriter.getConstantMembers,
//      cmdWriter.getConstantMembers,
//      eventWriter.getConstantMembers,
//      tlmWriter.getConstantMembers,
//      paramWriter.getConstantMembers
//    ).flatten
//
//    if constants.isEmpty then Nil
//    else List(
//      List(
//        linesClassMember(
//          List(
//            CppDocHppWriter.writeAccessTag("PROTECTED"),
//            CppDocWriter.writeBannerComment(
//              "Constants"
//            ),
//          ).flatten
//        )
//      ),
//      constants
//    ).flatten
//  }
//
//  private def getFriendClassMembers: List[CppDoc.Class.Member] = {
//    List(
//      linesClassMember(
//        List(
//          CppDocWriter.writeBannerComment(
//            "Friend classes"
//          ),
//          lines(
//            s"""|
//                |//! Friend class for white-box testing
//                |friend class ${className}Friend;
//                |"""
//          )
//        ).flatten
//      )
//    )
//  }
//
//  private def getComponentFunctionMembers: List[CppDoc.Class.Member] = {
//    def writeChannelInit(channel: TlmChannel) = {
//      List(
//        lines(
//          s"""|// Write telemetry channel ${channel.getName}
//              |this->${channelUpdateFlagName(channel.getName)} = true;
//              |"""
//        ),
//        channel.channelType match {
//          case t if s.isPrimitive(t, writeChannelType(t)) => lines(
//            s"this->${channelStorageName(channel.getName)} = 0;"
//          )
//          case _ => Nil
//        }
//      ).flatten
//    }
//    def writePortConnections(port: PortInstance) = {
//      val name = port.getUnqualifiedName
//      val d = port.getDirection.get
//
//      line(s"// Connect ${d.toString} port $name") ::
//        wrapInForLoopStaggered(
//          "PlatformIntType port = 0",
//          s"port < static_cast<PlatformIntType>(this->${portNumGetterName(name, d)}())",
//          "port++",
//          List(
//            lines(
//              s"|this->${portVariableName(name, d)}[port].init();"
//            ),
//            d match {
//              case PortInstance.Direction.Input => lines(
//                s"""|this->${portVariableName(name, d)}[port].addCallComp(
//                    |  this,
//                    |  ${inputPortCallbackName(name)}
//                    |);
//                    |this->${portVariableName(name, d)}[port].setPortNum(port);
//                    |"""
//              )
//              case PortInstance.Direction.Output => Nil
//            },
//            Line.blank :: lines(
//              s"""|#if FW_OBJECT_NAMES == 1
//                  |char portName[120];
//                  |(void) snprintf(
//                  |  portName,
//                  |  sizeof(portName),
//                  |  "%s_${name}_${d.toString.capitalize}Port[%" PRI_PlatformIntType "]",
//                  |  this->m_objName,
//                  |  port
//                  |);
//                  |this->${portVariableName(name, d)}[port].setObjName(portName);
//                  |#endif
//                  |"""
//            )
//          ).flatten
//        )
//    }
//
//    val initInstanceParam = List(
//      CppDoc.Function.Param(
//        CppDoc.Type("NATIVE_INT_TYPE"),
//        "instance",
//        Some("The instance number"),
//        Some("0")
//      )
//    )
//    val initQueueDepthParam =
//      if data.kind != Ast.ComponentKind.Passive then List(
//        CppDoc.Function.Param(
//          CppDoc.Type("NATIVE_INT_TYPE"),
//          "queueDepth",
//          Some("The queue depth")
//        )
//      )
//      else Nil
//    val initMsgSizeParam =
//      if hasSerialAsyncInputPorts then List(
//        CppDoc.Function.Param(
//          CppDoc.Type("NATIVE_INT_TYPE"),
//          "msgSize",
//          Some("The message size")
//        )
//      )
//      else Nil
//
//    List(
//      writeAccessTagAndComment(
//        "PROTECTED",
//        "Component construction, initialization, and destruction"
//      ),
//      List(
//        constructorClassMember(
//          Some(s"Construct $className object"),
//          List(
//            CppDoc.Function.Param(
//              CppDoc.Type("const char*"),
//              "compName",
//              Some("The component name"),
//              Some("\"\"")
//            )
//          ),
//          List(s"Fw::${kindStr}ComponentBase(compName)"),
//          intersperseBlankLines(
//            List(
//              intersperseBlankLines(
//                updateOnChangeChannels.map((_, channel) =>
//                  writeChannelInit(channel)
//                )
//              ),
//              throttledEvents.map((_, event) => line(
//                s"this->${eventThrottleCounterName(event.getName)} = 0;"
//              )),
//              sortedParams.map((_, param) => line(
//                s"this->${paramValidityFlagName(param.getName)} = Fw::ParamValid::UNINIT;"
//              ))
//            )
//          )
//        ),
//        functionClassMember(
//          Some(s"Initialize $className object"),
//          "init",
//          initQueueDepthParam ++ initMsgSizeParam ++ initInstanceParam,
//          CppDoc.Type("void"),
//          intersperseBlankLines(
//            List(
//              lines(
//                s"""|// Initialize base class
//                    |Fw::$baseClassName::init(instance);
//                    |"""
//              ),
//              intersperseBlankLines(
//                List(
//                  intersperseBlankLines(specialInputPorts.map(writePortConnections)),
//                  intersperseBlankLines(typedInputPorts.map(writePortConnections)),
//                  intersperseBlankLines(serialInputPorts.map(writePortConnections)),
//                  intersperseBlankLines(specialOutputPorts.map(writePortConnections)),
//                  intersperseBlankLines(typedOutputPorts.map(writePortConnections)),
//                  intersperseBlankLines(serialOutputPorts.map(writePortConnections))
//                )
//              )
//            )
//          )
//        ),
//        destructorClassMember(
//          Some(s"Destroy $className object"),
//          Nil,
//          CppDoc.Class.Destructor.Virtual
//        )
//      )
//    ).flatten
//  }
//
//  private def getMutexOperationMembers: List[CppDoc.Class.Member] = {
//    if !hasGuardedInputPorts then Nil
//    else List(
//      writeAccessTagAndComment(
//        "PROTECTED",
//        "Mutex operations for guarded ports",
//        Some(
//          """|You can override these operations to provide more sophisticated
//             |synchronization
//             |"""
//        )
//      ),
//      List(
//        functionClassMember(
//          Some("Lock the guarded mutex"),
//          "lock",
//          Nil,
//          CppDoc.Type("void"),
//          Nil,
//          CppDoc.Function.Virtual
//        ),
//        functionClassMember(
//          Some("Unlock the guarded mutex"),
//          "unLock",
//          Nil,
//          CppDoc.Type("void"),
//          Nil,
//          CppDoc.Function.Virtual
//        )
//      )
//    ).flatten
//  }
//
//  private def getDispatchFunctionMember: List[CppDoc.Class.Member] = {
//    if data.kind == Ast.ComponentKind.Passive then Nil
//    else List(
//      writeAccessTagAndComment(
//        data.kind match {
//          case Ast.ComponentKind.Active => "PRIVATE"
//          case Ast.ComponentKind.Queued => "PROTECTED"
//          case _ => ""
//        },
//        "Message dispatch functions"
//      ),
//      List(
//        functionClassMember(
//          Some("Called in the message loop to dispatch a message from the queue"),
//          "doDispatch",
//          Nil,
//          CppDoc.Type(
//            "MsgDispatchStatus",
//            Some("Fw::QueuedComponentBase::MsgDispatchStatus")
//          ),
//          Nil,
//          CppDoc.Function.Virtual
//        )
//      )
//    ).flatten
//  }
//
//  private def getTimeFunctionMember: List[CppDoc.Class.Member] = {
//    if !hasTimeGetPort then Nil
//    else List(
//      writeAccessTagAndComment(
//        "PROTECTED",
//        "Time"
//      ),
//      List(
//        functionClassMember(
//          Some(
//            s"""| Get the time
//                |
//                |\\return The current time
//                |"""
//          ),
//          "getTime",
//          Nil,
//          CppDoc.Type("Fw::Time"),
//          Nil
//        )
//      )
//    ).flatten
//  }
//
//  private def getMsgSizeVariableMember: List[CppDoc.Class.Member] = {
//    if !hasSerialAsyncInputPorts then Nil
//    else List(
//      linesClassMember(
//        List(
//          CppDocHppWriter.writeAccessTag("PRIVATE"),
//          lines(
//            """|
//             |//! Stores max message size
//               |NATIVE_INT_TYPE m_msgSize;
//               |"""
//          )
//        ).flatten
//      )
//    )
//  }
//
//  private def getMutexVariableMembers: List[CppDoc.Class.Member] = {
//    if !(hasGuardedInputPorts|| hasParameters) then Nil
//    else List(
//      linesClassMember(
//        List(
//          CppDocHppWriter.writeAccessTag("PRIVATE"),
//          CppDocWriter.writeBannerComment(
//            "Mutexes"
//          ),
//          if !hasGuardedInputPorts then Nil
//          else lines(
//            """|
//               |//! Mutex for guarded ports
//               |Os::Mutex m_guardedPortMutex;
//               |"""
//          ),
//          if !hasParameters then Nil
//          else lines(
//            """|
//               |//! Mutex for locking parameters during sets and saves
//               |Os::Mutex m_paramLock;
//               |"""
//          )
//        ).flatten
//      )
//    )
//  }

}
