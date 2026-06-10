package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component input port instances */
case class ComponentInputPorts(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  val componentCommands = ComponentCommands(s, aNode)

  /** Generates the port getter functions for a component base class or tester base class */
  def generateGetters(
    ports: List[PortInstance],
    portType: String,
    getPortName: String => String,
    getGetterName: String => String,
    getNumGetterName: PortInstance => String,
    getVariableName: PortInstance => String
  ): List[CppDoc.Class.Member] = addAccessTagAndComment(
    "public",
    s"Getters for $portType ports",
    getPortMembersWithGuard(
      ports,
      p => List(
        generateGetterForPort(
          p,
          portType,
          getPortName(p.getUnqualifiedName),
          getGetterName(p.getUnqualifiedName),
          getNumGetterName(p),
          getVariableName(p)
        )
      )
    )
  )

  /** Generates the port handler functions for a component base class or tester base class */
  def generateHandlers(
    ports: List[PortInstance],
    getPortName: String => String,
    getHandlerName: String => String
  ): List[CppDoc.Class.Member] = {
    ports.map(p =>
      functionClassMember(
        Some(s"Handler for input port ${getPortName(p.getUnqualifiedName)}"),
        getHandlerName(p.getUnqualifiedName),
        portNumParam :: getPortFunctionParams(p),
        getPortReturnTypeAsCppDocType(p),
        Nil,
        CppDoc.Function.PureVirtual
      )
    )
  }

  /** Gets the callback functions for a component base class */
  def getCallbacks(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    val functions = getPortMembersWithGuard(ports, p => List(getCallbackForPort(p)))
    addAccessTagAndComment(
      "private",
      s"Calls for messages received on ${getPortListTypeString(ports)} input ports",
      guardedList (!ports.isEmpty) (
        ports.head.getType.get match {
          case PortInstance.Type.DefPort(_) => functions
          case PortInstance.Type.Serial =>
            wrapClassMembersInIfDirective("#if FW_PORT_SERIALIZATION", functions)
        }
      )
    )
  }

  /** Gets the handler base functions for a component base class */
  def getHandlerBases(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    lazy val comment =
      s"""|Port handler base-class functions for ${getPortListTypeString(ports)} input ports
          |
          |Call these functions directly to bypass the corresponding ports
          |"""
    val handlerBases = ports.map(getHandlerBaseForPort)
    guardedList (!handlerBases.isEmpty) (
      linesClassMember(
        lines(
          """|
             |#if FW_DIRECT_PORT_CALLS
             |public:
             |#else
             |protected:
             |#endif
             |"""
        ).map(_.indentOut(2))
      ) ::
      addComment(comment, handlerBases)
    )
  }

  /** Gets the overflow hooks for a component base class */
  def getOverflowHooks(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      s"""|Hooks for ${getPortListTypeString(ports)} async input ports
          |
          |Each of these functions is invoked when placing a message on the
          |queue would cause the queue to overlow. You should override them to provide
          |specific overflow behavior.
          |""",
      ports.map(
        p => getVirtualOverflowHook(
          p.getUnqualifiedName,
          MessageType.Port,
          portNumParam :: getPortFunctionParams(p)
        )
      ),
      CppDoc.Lines.Hpp
    )
  }

  /** Gets the port getter functions for a component base class */
  def getGetters(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    val typeString = getPortListTypeString(ports)
    wrapClassMembersInIfDirective(
      "#if !FW_DIRECT_PORT_CALLS",
      generateGetters(
        ports,
        s"$typeString input",
        (s: String) => s,
        inputPortGetterName,
        portNumGetterName,
        portVariableName
      )
    )
  }

  /** Gets the port handler functions for a component base class */
  def getHandlers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    val typeString = getPortListTypeString(ports)
    addAccessTagAndComment(
      "protected",
      s"Handlers to implement for $typeString input ports",
      generateHandlers(
        ports,
        (s: String) => s,
        inputPortHandlerName
      ),
      CppDoc.Lines.Hpp
    )
  }

  /** Gets the pre-message hooks for a component base class */
  def getPreMsgHooks(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      s"""|Pre-message hooks for ${getPortListTypeString(ports)} async input ports
          |
          |Each of these functions is invoked just before processing a message
          |on the corresponding port. By default, they do nothing. You can
          |override them to provide specific pre-message behavior.
          |""",
      ports.map(p =>
        functionClassMember(
          Some(s"Pre-message hook for async input port ${p.getUnqualifiedName}"),
          inputPortHookName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          CppDoc.Type("void"),
          lines("// Default: no-op"),
          CppDoc.Function.Virtual
        )
      )
    )
  }

  // Generates the getter function for a port
  private def generateGetterForPort(
    p: PortInstance,
    portType: String,
    portName:String,
    getterName: String,
    numGetterName: String,
    variableName: String
  ) = functionClassMember(
    Some(
      s"""|Get $portType port at index
          |
          |\\return $portName[portNum]
          |"""
    ),
    getterName,
    List(portNumParam),
    CppDoc.Type(s"${getQualifiedPortTypeName(p, PortInstance.Direction.Input)}*"),
    lines(
      s"""|FW_ASSERT(
          |  (0 <= portNum) && (portNum < this->$numGetterName()),
          |  static_cast<FwAssertArgType>(portNum)
          |);
          |
          |return &this->$variableName[portNum];
          |"""
    )
  )

  // Gets the callback function for a port
  private def getCallbackForPort(p: PortInstance) = functionClassMember(
    Some(s"Callback for port ${p.getUnqualifiedName}"),
    inputPortCallbackName(p.getUnqualifiedName),
    List(
      CppDoc.Function.Param(
        CppDoc.Type("Fw::PassiveComponentBase*"),
        "callComp",
        Some("The component instance")
      ),
      portNumParam
    ) ++ getPortFunctionParams(p),
    getPortReturnTypeAsCppDocType(p),
    writeInputPortHandlerCall(p),
    CppDoc.Function.Static
  )

  // Gets the handler base function for a port
  private def getHandlerBaseForPort(p: PortInstance) = {
    val comment = s"Handler base-class function for input port ${p.getUnqualifiedName}"
    p.getSpecialKind match {
      case Some(Ast.SpecPortInstance.CommandRecv) =>
        componentCommands.getHandlerBaseForCommandPort(comment, p)
      case _ => getHandlerBaseForNonCommandPort(comment, p)
    }
  }

  private def getHandlerBaseForNonCommandPort(comment: String, p: PortInstance) = {
    val params = getPortParams(p)
    val returnType = getPortReturnTypeAsStringOption(p)
    val retValAssignment = returnType match {
      case Some(_) => s"retVal = "
      case None => ""
    }
    val portName = p.getUnqualifiedName
    val handlerName = inputPortHandlerName(portName)
    lazy val handlerCall =
      line("// Call handler function") ::
        writeFunctionCall(
          s"${retValAssignment}this->$handlerName",
          List("portNum"),
          params.map(_._1)
        )
    lazy val guardedHandlerCall =
      List.concat(
        lines(
          """|// Lock guard mutex before calling
             |this->lock();
             |"""
        ),
        Line.blank :: handlerCall,
        lines(
          """|
             |// Unlock guard mutex
             |this->unLock();
             |"""
        )
      )
    functionClassMember(
      Some(comment),
      inputPortHandlerBaseName(p.getUnqualifiedName),
      portNumParam :: getPortFunctionParams(p),
      getPortReturnTypeAsCppDocType(p),
      intersperseBlankLines(
        List(
          lines(
            s"""|// Make sure port number is valid
                |FW_ASSERT(
                |  (0 <= portNum) && (portNum < this->${portNumGetterName(p)}()),
                |  static_cast<FwAssertArgType>(portNum)
                |);
                |"""
          ),
          returnType.map(tn => lines(s"$tn retVal;")).getOrElse(Nil),
          p match {
            case i: PortInstance.General => i.kind match {
              case PortInstance.General.Kind.AsyncInput(priority, queueFull) =>
                writeAsyncHandlerCall(i, params, queueFull, priority)
              case PortInstance.General.Kind.GuardedInput => guardedHandlerCall
              case PortInstance.General.Kind.SyncInput => handlerCall
              case _ => Nil
            }
            case special: PortInstance.Special => special.specifier.inputKind match {
              case Some(Ast.SpecPortInstance.Async) =>
                writeAsyncHandlerCall(
                  special, params, special.queueFull.get,
                  special.priority
                )
              case Some(Ast.SpecPortInstance.Guarded) => guardedHandlerCall
              case Some(Ast.SpecPortInstance.Sync) => handlerCall
              case _ => Nil
            }
            case _ => Nil
          },
          returnType.map(_ => lines("return retVal;")).getOrElse(Nil)
        )
      )
    )
  }

  // Writes an async handler call
  private def writeAsyncHandlerCall(
      p: PortInstance,
      params: List[(String, String, Option[Type])],
      queueFull: Ast.QueueFull,
      priority: Option[BigInt]
  ) = {
    val bufferName = p.getType.get match {
      case PortInstance.Type.DefPort(_) => "msg"
      case PortInstance.Type.Serial => "msgSerBuff"
    }
    intersperseBlankLines(
      List(
        p.getType.get match {
          case PortInstance.Type.DefPort(_) => List.concat(
            line("// Call pre-message hook") ::
              writeFunctionCall(
                s"${inputPortHookName(p.getUnqualifiedName)}",
                List("portNum"),
                params.map(_._1)
              ),
            lines(
              s"""|ComponentIpcSerializableBuffer $bufferName;
                  |Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
                  |"""
            )
          )
          case PortInstance.Type.Serial => lines(
            s"""|// Declare buffer for ${p.getUnqualifiedName}
                |U8 msgBuff[this->m_msgSize];
                |Fw::ExternalSerializeBuffer $bufferName(
                |  msgBuff,
                |  static_cast<Fw::Serializable::SizeType>(this->m_msgSize)
                |);
                |Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
                |"""
          )
        },
        lines(
          s"""|// Serialize message ID
              |_status = $bufferName.serializeFrom(
              |  static_cast<FwEnumStoreType>(${portCppConstantName(p)})
              |);
              |FW_ASSERT(
              |  _status == Fw::FW_SERIALIZE_OK,
              |  static_cast<FwAssertArgType>(_status)
              |);
              |
              |// Serialize port number
              |_status = $bufferName.serializeFrom(portNum);
              |FW_ASSERT(
              |  _status == Fw::FW_SERIALIZE_OK,
              |  static_cast<FwAssertArgType>(_status)
              |);
              |"""
        ),
        intersperseBlankLines(
          getPortParams(p).map((n, _, tyOpt) => {
            val serializeExpr = tyOpt match {
              case Some(t: Type.String) =>
                val serialSize = writeStringSize(s, t)
                s"$n.serializeTo($bufferName, $serialSize)"
              case _ => s"$bufferName.serializeFrom($n)"
            }
            lines(
            s"""|// Serialize argument $n
                |_status = $serializeExpr;
                |FW_ASSERT(
                |  _status == Fw::FW_SERIALIZE_OK,
                |  static_cast<FwAssertArgType>(_status)
                |);
                |"""
            )
          })
        ),
        writeSendMessageLogic(
          bufferName,
          queueFull,
          priority,
          MessageType.Port,
          p.getUnqualifiedName,
          portNumParam :: getPortFunctionParams(p)
        )
      )
    )
  }

  // Writes the handler call for an input port
  private def writeInputPortHandlerCall(p: PortInstance) = {
    val params = getPortParams(p)
    val nonVoidReturn = getPortReturnTypeAsStringOption(p).isDefined
    val addReturnPrefix = addConditionalPrefix (nonVoidReturn) ("return")
    val handlerBaseName = inputPortHandlerBaseName(p.getUnqualifiedName)
    List.concat(
      lines(
        s"""|FW_ASSERT(callComp);
            |$componentClassName* compPtr = static_cast<$componentClassName*>(callComp);
            |
            |"""
      ),
      writeFunctionCall(
        addReturnPrefix(s"compPtr->$handlerBaseName"),
        List("portNum"),
        params.map(_._1)
      )
    )
  }

}
