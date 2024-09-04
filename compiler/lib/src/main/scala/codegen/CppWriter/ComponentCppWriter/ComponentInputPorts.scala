package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component input port instances */
case class ComponentInputPorts(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def generateHandlers(
    ports: List[PortInstance],
    portName: String => String,
    handlerName: String => String
  ): List[CppDoc.Class.Member] = {
    ports.map(p =>
      functionClassMember(
        Some(s"Handler for input port ${portName(p.getUnqualifiedName)}"),
        handlerName(p.getUnqualifiedName),
        portNumParam :: getPortFunctionParams(p),
        getPortReturnTypeAsCppDocType(p),
        Nil,
        CppDoc.Function.PureVirtual
      )
    )
  }

  def generateGetters(
    ports: List[PortInstance],
    portType: String,
    portName: String => String,
    getterName: String => String,
    numGetterName: PortInstance => String,
    variableName: PortInstance => String
  ): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "public",
      s"Getters for $portType ports",
      mapPorts(ports, p => List(
        functionClassMember(
          Some(
            s"""|Get $portType port at index
                |
                |\\return ${portName(p.getUnqualifiedName)}[portNum]
                |"""
          ),
          getterName(p.getUnqualifiedName),
          List(
            portNumParam
          ),
          CppDoc.Type(s"${getQualifiedPortTypeName(p, PortInstance.Direction.Input)}*"),
          lines(
            s"""|FW_ASSERT(
                |  portNum < this->${numGetterName(p)}(),
                |  static_cast<FwAssertArgType>(portNum)
                |);
                |
                |return &this->${variableName(p)}[portNum];
                |"""
          )
        )
      ))
    )
  }

  def getGetters(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    val typeStr = getPortListTypeString(ports)

    generateGetters(
      ports,
      s"$typeStr input",
      (s: String) => s,
      inputPortGetterName,
      portNumGetterName,
      portVariableName
    )
  }

  def getHandlers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      s"Handlers to implement for ${getPortListTypeString(ports)} input ports",
      generateHandlers(
        ports,
        (s: String) => s,
        inputPortHandlerName
      ),
      CppDoc.Lines.Hpp
    )
  }

  def getHandlerBases(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    def writeAsyncInputPort(
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
                  |Fw::ExternalSerializeBuffer $bufferName(msgBuff, this->m_msgSize);
                  |Fw::SerializeStatus _status = Fw::FW_SERIALIZE_OK;
                  |"""
            )
          },
          lines(
            s"""|// Serialize message ID
                |_status = $bufferName.serialize(
                |  static_cast<FwEnumStoreType>(${portCppConstantName(p)})
                |);
                |FW_ASSERT(
                |  _status == Fw::FW_SERIALIZE_OK,
                |  static_cast<FwAssertArgType>(_status)
                |);
                |
                |// Serialize port number
                |_status = $bufferName.serialize(portNum);
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
                  s"$n.serialize($bufferName, $serialSize)"
                case _ => s"$bufferName.serialize($n)"
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

    addAccessTagAndComment(
      "PROTECTED",
      s"""|Port handler base-class functions for ${getPortListTypeString(ports)} input ports
          |
          |Call these functions directly to bypass the corresponding ports
          |""",
      ports.map(p => {
        val params = getPortParams(p)
        val returnType = getPortReturnType(p)
        val retValAssignment = returnType match {
          case Some(_) => s"retVal = "
          case None => ""
        }
        val portName = p.getUnqualifiedName
        val handlerName = inputPortHandlerName(portName)
        def handlerCall =
          line("// Call handler function") ::
            writeFunctionCall(
              s"${retValAssignment}this->$handlerName",
              List("portNum"),
              params.map(_._1)
            )
        def guardedHandlerCall =
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
          Some(s"Handler base-class function for input port ${p.getUnqualifiedName}"),
          inputPortHandlerBaseName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          getPortReturnTypeAsCppDocType(p),
          intersperseBlankLines(
            List(
              lines(
                s"""|// Make sure port number is valid
                    |FW_ASSERT(
                    |  portNum < this->${portNumGetterName(p)}(),
                    |  static_cast<FwAssertArgType>(portNum)
                    |);
                    |"""
              ),
              returnType match {
                case Some(tn) => lines(s"$tn retVal;")
                case None => Nil
              },
              p match {
                case i: PortInstance.General => i.kind match {
                  case PortInstance.General.Kind.AsyncInput(priority, queueFull) =>
                    writeAsyncInputPort(i, params, queueFull, priority)
                  case PortInstance.General.Kind.GuardedInput => guardedHandlerCall
                  case PortInstance.General.Kind.SyncInput => handlerCall
                  case _ => Nil
                }
                case special: PortInstance.Special => special.specifier.inputKind match {
                  case Some(Ast.SpecPortInstance.Async) =>
                    writeAsyncInputPort(
                      special, params, special.queueFull.get,
                      special.priority
                    )
                  case Some(Ast.SpecPortInstance.Guarded) => guardedHandlerCall
                  case Some(Ast.SpecPortInstance.Sync) => handlerCall
                  case _ => Nil
                }
                case _ => Nil
              },
              returnType match {
                case Some(_) => lines("return retVal;")
                case None => Nil
              }
            )
          )
        )
      })
    )
  }

  def getCallbacks(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    def writeInputPort(p: PortInstance) = {
      val params = getPortParams(p)
      val returnKeyword = getPortReturnType(p) match {
        case Some(_) => "return "
        case None => ""
      }

      List(
        lines(
          s"""|FW_ASSERT(callComp);
              |$className* compPtr = static_cast<$className*>(callComp);
              |
              |"""
        ),
        writeFunctionCall(
          s"${returnKeyword}compPtr->${inputPortHandlerBaseName(p.getUnqualifiedName)}",
          List("portNum"),
          params.map(_._1)
        )
      ).flatten
    }
    def writeCommandInputPort() = {
      if !hasCommands then lines(
        """|FW_ASSERT(callComp);
           |
           |const U32 idBase = callComp->getIdBase();
           |FW_ASSERT(opCode >= idBase, static_cast<FwAssertArgType>(opCode), static_cast<FwAssertArgType>(idBase));
           |"""
      )
      else List(
        lines(
          s"""|FW_ASSERT(callComp);
              |$className* compPtr = static_cast<$className*>(callComp);
              |
              |const U32 idBase = callComp->getIdBase();
              |FW_ASSERT(opCode >= idBase, static_cast<FwAssertArgType>(opCode), static_cast<FwAssertArgType>(idBase));
              |
              |// Select base class function based on opcode
              |"""
        ),
        wrapInSwitch(
          "opCode - idBase",
          intersperseBlankLines(
            sortedCmds.map((_, cmd) =>
              wrapInScope(
                s"case ${commandConstantName(cmd)}: {",
                cmd match {
                  case _: Command.NonParam => lines(
                    s"""|compPtr->${commandHandlerBaseName(cmd.getName)}(
                        |  opCode,
                        |  cmdSeq,
                        |  args
                        |);
                        |break;
                        |""".stripMargin
                  )
                  case c: Command.Param =>
                    val args =
                      c.kind match {
                        case Command.Param.Set => "args"
                        case Command.Param.Save => ""
                      }

                    lines(
                      s"""|Fw::CmdResponse _cstat = compPtr->${paramHandlerName(c.aNode._2.data.name, c.kind)}($args);
                          |compPtr->cmdResponse_out(
                          |  opCode,
                          |  cmdSeq,
                          |  _cstat
                          |);
                          |break;
                          |"""
                    )
                },
                "}"
              )
            )
          )
        )
      ).flatten
    }

    val functions =
      mapPorts(ports, p => {
        List(
          functionClassMember(
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
            p match {
              case i: PortInstance.General => writeInputPort(i)
              case special: PortInstance.Special =>
                special.specifier.kind match {
                  case Ast.SpecPortInstance.CommandRecv => writeCommandInputPort()
                  case Ast.SpecPortInstance.ProductRecv => writeInputPort(special)
                  case _ => Nil
                }
              case _ => Nil
            },
            CppDoc.Function.Static
          )
        )
      })

    addAccessTagAndComment(
      "PRIVATE",
      s"Calls for messages received on ${getPortListTypeString(ports)} input ports",
      ports match {
        case Nil => Nil
        case _ => ports.head.getType.get match {
          case PortInstance.Type.DefPort(_) => functions
          case PortInstance.Type.Serial =>
            wrapClassMembersInIfDirective(
              "\n#if FW_PORT_SERIALIZATION",
              functions
            )
        }
      }
    )
  }

  def getPreMsgHooks(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
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

  def getOverflowHooks(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
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

  // Get the name for a param command handler function
  private def paramCmdHandlerName(cmd: Command.Param) =
    s"param${getCmdParamKindString(cmd.kind).capitalize}_${cmd.getName}"

}
