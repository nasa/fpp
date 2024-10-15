package fpp.compiler.codegen
import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

case class ComponentInternalStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  // ----------------------------------------------------------------------
  // Public constants
  // ----------------------------------------------------------------------

  val hookInstances =
    internalStateMachineInstances.filter(_.queueFull == Ast.QueueFull.Hook)

  val hasHookInstances = hookInstances.nonEmpty

  val signals = internalSmSymbols.flatMap(
    smSymbol => s.a.stateMachineMap(smSymbol).signals
  )

  val hasSignals = signals.nonEmpty

  // ----------------------------------------------------------------------
  // Public functions
  // ----------------------------------------------------------------------

  /** Gets the anonymous namespace lines */
  def getAnonymousNamespaceLines: List[Line] =
    guardedList (hasInternalStateMachineInstances) (
      SignalBufferWriter.getLines
    )

  /** Gets the name of a component action function */
  def getComponentActionFunctionName(
    sm: Symbol.StateMachine,
    action: StateMachineSymbol.Action
  ): String = {
    val implName = writeStateMachineImplType(sm)
    val baseName = getSmActionFunctionName(sm, action)
    s"${implName}_$baseName"
  }

  /** Gets the parameters of a component action function */
  def getComponentActionFunctionParams(
    sm: Symbol.StateMachine,
    action: StateMachineSymbol.Action
  ) = getComponentParamsWithTypeNameOpt(sm, action.node._2.data.typeName)

  /** Gets the name of a component guard function */
  def getComponentGuardFunctionName(
    sm: Symbol.StateMachine,
    guard: StateMachineSymbol.Guard
  ): String = {
    val implName = writeStateMachineImplType(sm)
    val baseName = getSmGuardFunctionName(sm, guard)
    s"${implName}_$baseName"
  }

  /** Gets the parameters of a component guard function */
  def getComponentGuardFunctionParams(
    sm: Symbol.StateMachine,
    guard: StateMachineSymbol.Guard
  ) = getComponentParamsWithTypeNameOpt(sm, guard.node._2.data.typeName)

  /** Gets the private function members */
  def getPrivateFunctionMembers: List[CppDoc.Class.Member] = List.concat(
    getSendSignalHelperFunctions,
    getSmDispatchHelperFunctions
  )

  /** Gets the protected function members */
  def getProtectedFunctionMembers: List[CppDoc.Class.Member] = List.concat(
    getStateGetterFunctions,
    getSendSignalFunctions,
    getOverflowHooks,
    getVirtualActions,
    getVirtualGuards
  )

  /** Gets the type members */
  def getTypeMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Types for internal state machines",
      getStateMachines
    )

  /** Writes the dispatch case, if any, for internal state machine instances */
  def writeDispatchCase: List[Line] =
    guardedList (hasInternalStateMachineInstances) (
      CppDocWriter.writeComment("Handle signals to internal state machines") ++
      lines(
        s"""|case $internalStateMachineMsgType:
            |  this->smDispatch(msg);
            |  break;"""
      )
    )

  // ----------------------------------------------------------------------
  // Public overrides
  // ----------------------------------------------------------------------

  override def writeQueueFullLines(
    queueFull: Ast.QueueFull,
    messageType: MessageType,
    hookBaseName: String,
    hookParams: List[CppDoc.Function.Param]
  ): List[Line] =
    queueFull match {
      case Ast.QueueFull.Hook =>
        val hookName = inputOverflowHookName(hookBaseName, messageType)
        wrapInIf(
          "qStatus == Os::Queue::Status::FULL",
          lines(
            s"""|
                |// Deserialize the state machine ID and signal
                |FwEnumStoreType smId;
                |FwEnumStoreType signal;
                |$componentClassName::deserializeSmIdAndSignal(buffer, smId, signal);
                |
                |// Call the overflow hook
                |this->$hookName(static_cast<SmId>(smId), signal, buffer);
                |
                |// Continue execution
                |return;"""
          ) :+ Line.blank
        )
      case _ => super.writeQueueFullLines(
        queueFull,
        messageType,
        hookBaseName,
        hookParams
      )
    }

  // ----------------------------------------------------------------------
  // Private functions
  // ----------------------------------------------------------------------

  private def getComponentParamsWithTypeNameOpt(
    sm: Symbol.StateMachine,
    typeNameOpt: Option[AstNode[Ast.TypeName]]
  ) = {
    object Utils extends StateMachineCppWriterUtils(s, sm.node)
    val smName = writeStateMachineImplType(sm)
    val signalParam = CppDoc.Function.Param(
      CppDoc.Type(s"${smName}::Signal"),
      Utils.signalParamName,
      Some("The signal")
    )
    val valueParams = Utils.getValueParamsWithTypeNameOpt(typeNameOpt)
    stateMachineIdParam :: signalParam :: valueParams
  }

  private def getDeserializeSmIdAndSignal: CppDoc.Class.Member =
    functionClassMember(
      Some("Deserialize the state machine ID and signal from the message buffer"),
      "deserializeSmIdAndSignal",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("Fw::SerializeBufferBase&"),
          "buffer",
          Some("The message buffer (input and output)")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("FwEnumStoreType&"),
          "smId",
          Some("The state machine ID (output)")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("FwEnumStoreType&"),
          "signal",
          Some("The signal (output)")
        )
      ),
      CppDoc.Type("void"),
      lines(
        s"""|// Move deserialization beyond the message type and port number
            |Fw::SerializeStatus status =
            |  buffer.moveDeserToOffset(ComponentIpcSerializableBuffer::DATA_OFFSET);
            |FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));
            |
            |// Deserialize the state machine ID
            |status = buffer.deserialize(smId);
            |FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));
            |
            |// Deserialize the signal
            |status = buffer.deserialize(signal);
            |FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));"""
      ),
      CppDoc.Function.Static
    )

  private def getGeneralSmDispatchFunction =
    functionClassMember(
      Some("Dispatch a signal to a state machine instance"),
      "smDispatch",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("Fw::SerializeBufferBase&"),
          "buffer",
          Some("The message buffer")
        )
      ),
      CppDoc.Type("void"),
      lines(
        s"""|// Deserialize the state machine ID and signal
            |FwEnumStoreType storedSmId;
            |FwEnumStoreType storedSignal;
            |$componentClassName::deserializeSmIdAndSignal(buffer, storedSmId, storedSignal);"""
      ) ++
      CppDocWriter.writeComment("Select the target state machine instance") ++
      lines("const SmId smId = static_cast<SmId>(storedSmId);") ++
      wrapInSwitch(
        "smId",
        internalStateMachineInstances.flatMap(
          smi => {
            val smName = writeStateMachineImplType(smi.symbol)
            val smiName = smi.getName
            val smIdName = writeSmIdName(smiName)
            lines(
              s"""|case $smIdName: {
                  |  const $smName::Signal signal = static_cast<$smName::Signal>(storedSignal);
                  |  this->${smName}_smDispatch(buffer, this->m_stateMachine_$smiName, signal);
                  |  break;
                  |}"""
            )
          }
        ) ++
        lines(
          """|default:
             |  FW_ASSERT(0, static_cast<FwAssertArgType>(smId));
             |  break;"""
        )
      )
    )

  private def getOverflowHooks: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      """|Overflow hooks for internal state machine instances
         |
         |When sending a signal to a state machine instance, if
         |the queue overflows and the instance is marked with 'hook' behavior,
         |the corresponding function here is called.""",
      hookInstances.map(
        smi => getVirtualOverflowHook(
          smi.getName,
          MessageType.StateMachine,
          ComponentInternalStateMachines.hookParams
        )
      ),
      CppDoc.Lines.Hpp
    )

  private def getSendSignalFinishFunction(smi: StateMachineInstance): CppDoc.Class.Member = {
    functionClassMember(
      Some("Finish sending a signal to a state machine"),
      s"${smi.getName}_sendSignalFinish",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("Fw::SerializeBufferBase&"),
          "buffer",
          Some("The buffer with the data to send")
        )
      ),
      CppDoc.Type("void"),
      writeSendMessageLogic(
        "buffer", smi.queueFull, smi.priority,
        MessageType.StateMachine, smi.getName,
        Nil
      )
    )
  }

  private def getSendSignalFinishFunctions: List[CppDoc.Class.Member] =
    internalStateMachineInstances.map(getSendSignalFinishFunction)

  private def getSendSignalFunction(
    smi: StateMachineInstance,
    signal: StateMachineSymbol.Signal
  ): CppDoc.Class.Member = {
    val smName = writeStateMachineImplType(smi.symbol)
    val signalUnqualifiedName = signal.getUnqualifiedName
    val smiName = smi.getName
    val smIdName = writeSmIdName(smiName)
    val signalQualifiedName = s"$smName::Signal::$signalUnqualifiedName"
    val signalArg = s"static_cast<FwEnumStoreType>($signalQualifiedName)"
    object Utils extends StateMachineCppWriterUtils(s, smi.symbol.node)
    val params = Utils.getValueParamsWithTypeNameOpt(signal.node._2.data.typeName)
    functionClassMember(
      Some(s"Send signal $signalUnqualifiedName to state machine $smiName"),
      s"${smiName}_sendSignal_${signalUnqualifiedName}",
      params,
      CppDoc.Type("void"),
      List.concat(
        lines(
          s"""|ComponentIpcSerializableBuffer buffer;
              |// Serialize the message type, port number, state ID, and signal
              |this->sendSignalStart($smIdName, $signalArg, buffer);"""
        ),
        params match {
          case head :: _ =>
            val paramName = head.name
            val id = signal.node._2.data.typeName.get.id
            val serializeExpr = s.a.typeMap(id) match {
              case t: Type.String =>
                val serialSize = writeStringSize(s, t)
                s"$paramName.serialize(buffer, $serialSize)"
              case _ => s"buffer.serialize($paramName)"
            }
            lines(
              s"""|// Serialize the signal data
                  |const Fw::SerializeStatus status = $serializeExpr;
                  |FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));"""
            )
          case _ => Nil
        },
        lines(
          s"""|// Send the message and handle overflow
              |this->${smiName}_sendSignalFinish(buffer);"""
        )
      )
    )
  }

  private def getSendSignalFunctions: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Signal send functions",
      internalStateMachineInstances.flatMap(
        smi => {
          val sm = s.a.stateMachineMap(smi.symbol)
          sm.signals.map(getSendSignalFunction(smi, _))
        }
      )
    )

  private def getSendSignalHelperFunctions: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      "Send signal helper functions",
      guardedList (hasSignals) (
        getSendSignalStartFunction ::
        getSendSignalFinishFunctions
      )
    )

  private def getSendSignalStartFunction: CppDoc.Class.Member =
    functionClassMember(
      Some("Start sending a signal to a state machine"),
      "sendSignalStart",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("SmId"),
          "smId",
          Some("The state machine ID (input)")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("FwEnumStoreType"),
          "signal",
          Some("The signal (input)")
        ),
        CppDoc.Function.Param(
          CppDoc.Type("Fw::SerializeBufferBase&"),
          "buffer",
          Some("The message buffer (output)")
        )
      ),
      CppDoc.Type("void"),
      lines(
        s"""|Fw::SerializeStatus status = Fw::FW_SERIALIZE_OK;
            |
            |// Serialize the message type
            |status = buffer.serialize(static_cast<FwEnumStoreType>($internalStateMachineMsgType));
            |FW_ASSERT (status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));
            |
            |// Serialize the port number
            |status = buffer.serialize(static_cast<FwIndexType>(0));
            |FW_ASSERT (status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));
            |
            |// Serialize the state machine ID
            |status = buffer.serialize(static_cast<FwEnumStoreType>(smId));
            |FW_ASSERT (status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));
            |
            |// Serialize the signal
            |status = buffer.serialize(static_cast<FwEnumStoreType>(signal));
            |FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));"""
      )
    )

  private def getSmActionFunctionName(
    sm: Symbol.StateMachine,
    action: StateMachineSymbol.Action
  ): String = {
    object Utils extends StateMachineCppWriterUtils(s, sm.node)
    Utils.getActionFunctionName(action)
  }

  private def getSmActionFunctionParams(
    sm: Symbol.StateMachine,
    action: StateMachineSymbol.Action
  ): List[CppDoc.Function.Param] = {
    object Utils extends StateMachineCppWriterUtils(s, sm.node)
    Utils.getActionFunctionParams(action)
  }

  private def getSmDispatchHelperFunctions =
    addAccessTagAndComment(
      "PRIVATE",
      "Helper functions for state machine dispatch",
      guardedList (hasInternalStateMachineInstances) (
        getGeneralSmDispatchFunction ::
        getDeserializeSmIdAndSignal ::
        getSpecializedSmDispatchFunctions
      )
    )

  private def getSmGuardFunctionName(
    sm: Symbol.StateMachine,
    guard: StateMachineSymbol.Guard
  ): String = {
    object Utils extends StateMachineCppWriterUtils(s, sm.node)
    Utils.getGuardFunctionName(guard)
  }

  private def getSmGuardFunctionParams(
    sm: Symbol.StateMachine,
    guard: StateMachineSymbol.Guard
  ): List[CppDoc.Function.Param] = {
    object Utils extends StateMachineCppWriterUtils(s, sm.node)
    Utils.getGuardFunctionParams(guard)
  }

  private def getSpecializedSmDispatchFunction(
    smSymbol: Symbol.StateMachine
  ): CppDoc.Class.Member = {
    val smName = writeStateMachineImplType(smSymbol)
    val sm = s.a.stateMachineMap(smSymbol)
    val signals = sm.signals
    functionClassMember(
      Some(s"Dispatch a signal to a state machine instance of type $smName"),
      s"${smName}_smDispatch",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("Fw::SerializeBufferBase&"),
          "buffer",
          Some("The message buffer")
        ),
        CppDoc.Function.Param(
          CppDoc.Type(s"$smName&"),
          "sm",
          Some("The state machine")
        ),
        CppDoc.Function.Param(
          CppDoc.Type(s"$smName::Signal"),
          "signal",
          Some("The signal")
        )
      ),
      CppDoc.Type("void"),
      wrapInSwitch(
        "signal",
        signals.flatMap(
          signal => {
            val signalName = signal.getUnqualifiedName
            val astTypeNameOpt = signal.node._2.data.typeName
            val sendSignalArgs = astTypeNameOpt.map(_ => "value").getOrElse("")
            wrapInScope(
              s"case $smName::Signal::$signalName: {",
              List.concat(
                (
                  astTypeNameOpt match {
                    case Some(tn) =>
                      val t = s.a.typeMap(tn.id)
                      val cppTypeName = TypeCppWriter.getName(s, t)
                      lines(
                        s"""|// Deserialize the data
                            |${writeVarDecl(s, cppTypeName, "value", t)}
                            |const Fw::SerializeStatus status = buffer.deserialize(value);
                            |FW_ASSERT(status == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(status));"""
                      )
                    case None => Nil
                  }
                ),
                lines(
                  s"""|// Assert no data left in buffer
                      |FW_ASSERT(buffer.getBuffLeft() == 0, static_cast<FwAssertArgType>(buffer.getBuffLeft()));
                      |// Call the sendSignal function for sm and $signalName
                      |sm.sendSignal_$signalName($sendSignalArgs);
                      |break;"""
                )
              ),
              "}"
            )
          }
        ) ++
        lines(
          """|default:
             |  FW_ASSERT(0, static_cast<FwAssertArgType>(signal));
             |  break;"""
        )
      )
    )
  }

  private def getSpecializedSmDispatchFunctions =
    internalSmSymbols.map(getSpecializedSmDispatchFunction)

  private def getStateGetterFunction(smi: StateMachineInstance): CppDoc.Class.Member = {
    val smiName = smi.getName
    val smName = writeStateMachineImplType(smi.symbol)
    functionClassMember(
      Some(s"Get the state of state machine instance $smiName"),
      s"${smiName}_getState",
      Nil,
      CppDoc.Type(s"$smName::State", Some(s"$componentClassName::$smName::State")),
      lines(s"return this->m_stateMachine_$smiName.getState();"),
      CppDoc.Function.NonSV,
      CppDoc.Function.Const
    )
  }

  private def getStateGetterFunctions: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "State getter functions",
      internalStateMachineInstances.map(getStateGetterFunction)
    )

  private def getStateMachines: List[CppDoc.Class.Member] =
    internalSmSymbols.map(
      symbol => StateMachineWriter(symbol).getStateMachine
    )

  private def getVirtualAction (sm: StateMachine) (action: StateMachineSymbol.Action):
  CppDoc.Class.Member = {
    val smSymbol = sm.getSymbol
    val smName = writeStateMachineImplType(sm.getSymbol)
    val actionName = action.getUnqualifiedName
    functionClassMember(
      Some(
        addSeparatedString(
          s"Implementation for action $actionName of state machine $smName",
          AnnotationCppWriter.asStringOpt(action.node)
        )
      ),
      getComponentActionFunctionName(smSymbol, action),
      getComponentActionFunctionParams(smSymbol, action),
      CppDoc.Type("void"),
      Nil,
      CppDoc.Function.PureVirtual
    )
  }

  private def getVirtualActions: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Functions to implement for internal state machine actions",
      internalSmSymbols.flatMap(getVirtualActionsForSm),
      CppDoc.Lines.Hpp
    )

  private def getVirtualActionsForSm(smSymbol: Symbol.StateMachine):
  List[CppDoc.Class.Member] = {
    val sm = s.a.stateMachineMap(smSymbol)
    sm.actions.map(getVirtualAction (sm))
  }

  private def getVirtualGuard (sm: StateMachine) (guard: StateMachineSymbol.Guard):
  CppDoc.Class.Member = {
    val smSymbol = sm.getSymbol
    val smName = writeStateMachineImplType(sm.getSymbol)
    val guardName = guard.getUnqualifiedName
    functionClassMember(
      Some(
        addSeparatedString(
          s"Implementation for guard $guardName of state machine $smName",
          AnnotationCppWriter.asStringOpt(guard.node)
        )
      ),
      getComponentGuardFunctionName(smSymbol, guard),
      getComponentGuardFunctionParams(smSymbol, guard),
      CppDoc.Type("bool"),
      Nil,
      CppDoc.Function.PureVirtual,
      CppDoc.Function.Const
    )
  }

  private def getVirtualGuards: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Functions to implement for internal state machine guards",
      internalSmSymbols.flatMap(getVirtualGuardsForSm),
      CppDoc.Lines.Hpp
    )

  private def getVirtualGuardsForSm(smSymbol: Symbol.StateMachine):
  List[CppDoc.Class.Member] = {
    val sm = s.a.stateMachineMap(smSymbol)
    sm.guards.map(getVirtualGuard (sm))
  }

  private val stateMachineIdParam = CppDoc.Function.Param(
    CppDoc.Type("SmId"),
    "smId",
    Some("The state machine id")
  )

  /** Writes out the serialized size of the signal buffer */
  private object SignalBufferWriter extends ComponentCppWriterUtils(s, aNode) {

    def getLines: List[Line] = List.concat(
      CppDocWriter.writeComment("Constant definitions for the state machine signal buffer"),
      wrapInNamespace(
        "SmSignalBuffer",
        List.concat(
          guardedList (hasSignalTypes) (getSignalTypeUnion),
          getSerializedSizeConstant,
          List(Line.blank)
        )
      )
    )

    /** The signal types and the signal string size */
    private val signalTypesAndStringSize: (Set[Type], BigInt) =
      internalSmSymbols.foldLeft ((Set(), BigInt(0))) {
        case ((ts, maxStringSize), sym) => {
          val signals = s.a.stateMachineMap(sym).signals
          signals.foldLeft ((ts, maxStringSize)) {
            case ((ts, maxStringSize), signal) =>
              signal.node._2.data.typeName match {
                case Some(tn) =>
                  s.a.typeMap(tn.id) match {
                    case t: Type.String => (
                      ts + Type.String(None),
                      maxStringSize.max(getStringSize(s, t))
                    )
                    case t => (ts + t, maxStringSize)
                  }
                case None => (ts, maxStringSize)
              }
          }
        }
      }

    private val signalTypes: List[Type] =
      signalTypesAndStringSize._1.toList.sortBy(writeSignalTypeName)

    private val signalStringSize: BigInt = signalTypesAndStringSize._2

    private val hasSignalTypes: Boolean = !signalTypes.isEmpty

    private def getSerializedSizeConstant: List[Line] = {
      val comment = CppDocWriter.writeComment(
        "The serialized size"
      )
      val terms = "2 * sizeof(FwEnumStoreType)" ::
        (guardedList (hasSignalTypes) (List("sizeof(SignalTypeUnion)")))
      val sum = s"${terms.mkString(" +\n")};"
      val constantLines = line("static constexpr FwSizeType SERIALIZED_SIZE =") ::
        lines(sum).map(indentIn)
      List.concat(comment, constantLines)
    }

    private def getSignalTypeUnion: List[Line] = {
      val members = signalTypes.map (
        t => {
          val cppType = writeSignalTypeName(t)
          val typeIdent = cppType.replaceAll("::", "_")
          val sizeIdent = s"size_of_$typeIdent"
          val sizeExpr = writeSignalTypeSize(t)
          line(s"BYTE $sizeIdent[$sizeExpr];")
        }
      )
      val comment = CppDocWriter.writeComment(
        "Union for computing the max size of a signal type"
      )
      val union = wrapInScope("union SignalTypeUnion {", members, "};")
      List.concat(comment, union)
    }

    private def writeSignalTypeName(t: Type) = t match {
      case t: Type.String => "string"
      case _ => TypeCppWriter.getName(s, t)
    }

    private def writeSignalTypeSize(t: Type): String =
      t match {
        case _: Type.String =>
          s"Fw::StringBase::STATIC_SERIALIZED_SIZE(${signalStringSize.toString})"
        case _ => writeSerializedSizeExpr(s, t, TypeCppWriter.getName(s, t))
      }

  }

  /** Writes out the state machine implementations */
  private case class StateMachineWriter(smSymbol: Symbol.StateMachine)
    extends ComponentCppWriterUtils(s, aNode)
  {

    val stateMachine = s.a.stateMachineMap(smSymbol)

    val hasActionsOrGuards = stateMachine.hasActions || stateMachine.hasGuards

    val smBaseClassName = s"${s.writeSymbol(smSymbol)}StateMachineBase"

    val smClassName = writeStateMachineImplType(smSymbol)

    def getStateMachine: CppDoc.Class.Member =
      classClassMember(
        Some(s"Implementation of state machine ${smClassName}"),
        smClassName,
        Some(s"public $smBaseClassName"),
        List.concat(
          getConstructorMembers,
          getInitMembers,
          getGetterMembers,
          getActionMembers,
          getGuardMembers,
          getVariableMembers
        )
      )

    private def getActionMember(action: StateMachineSymbol.Action) = {
      val actionName = action.getUnqualifiedName
      val componentActionFunctionName =
        getComponentActionFunctionName(smSymbol, action)
      val args = writeComponentArgsWithTypeOpt(action.node._2.data.typeName)
      functionClassMember(
        Some(s"Implementation for action $actionName"),
        getSmActionFunctionName(smSymbol, action),
        getSmActionFunctionParams(smSymbol, action),
        CppDoc.Type("void"),
        lines(s"this->m_component.$componentActionFunctionName($args);")
      )
    }

    private def getActionMembers: List[CppDoc.Class.Member] =
      guardedList (stateMachine.hasActions) (
        linesClassMember(CppDocHppWriter.writeAccessTag("PRIVATE")) ::
          stateMachine.actions.map(getActionMember)
      )

    private def getConstructorMembers: List[CppDoc.Class.Member] = {
      lazy val members = List(
        linesClassMember(CppDocHppWriter.writeAccessTag("public")),
        constructorClassMember(
          Some("Constructor"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"$componentClassName&"),
              "component",
              Some("The enclosing component")
            )
          ),
          List("m_component(component)"),
          Nil
        )
      )
      guardedList (hasActionsOrGuards) (members)
    }

    private def getGetterMembers: List[CppDoc.Class.Member] =
      linesClassMember(CppDocHppWriter.writeAccessTag("public")) ::
      List(
        functionClassMember(
          Some(s"Get the state machine id"),
          "getId",
          Nil,
          CppDoc.Type(s"$componentClassName::SmId"),
          lines(s"return static_cast<$componentClassName::SmId>(this->m_id);"),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      )

    private def getGuardMember(guard: StateMachineSymbol.Guard) = {
      val guardName = guard.getUnqualifiedName
      val componentGuardFunctionName =
        getComponentGuardFunctionName(smSymbol, guard)
      val args = writeComponentArgsWithTypeOpt(guard.node._2.data.typeName)
      functionClassMember(
        Some(s"Implementation for guard $guardName"),
        getSmGuardFunctionName(smSymbol, guard),
        getSmGuardFunctionParams(smSymbol, guard),
        CppDoc.Type("bool"),
        lines(s"return this->m_component.$componentGuardFunctionName($args);"),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    }

    private def getGuardMembers: List[CppDoc.Class.Member] =
      guardedList (stateMachine.hasGuards) (
        linesClassMember(CppDocHppWriter.writeAccessTag("PRIVATE")) ::
          stateMachine.guards.map(getGuardMember)
      )

    private def getInitMembers: List[CppDoc.Class.Member] =
      List(
        linesClassMember(CppDocHppWriter.writeAccessTag("public")),
        functionClassMember(
          Some("Initialize the state machine"),
          "init",
          List(
            CppDoc.Function.Param(
              CppDoc.Type(s"$componentClassName::SmId"),
              "smId",
              Some("The state machine id")
            )
          ),
          CppDoc.Type("void"),
          lines("this->initBase(static_cast<FwEnumStoreType>(smId));")
        )
      )

    private def getVariableMembers: List[CppDoc.Class.Member] = {
      lazy val members =
        linesClassMember(CppDocHppWriter.writeAccessTag("PRIVATE")) ::
        List(
          linesClassMember(
            lines(
              s"""|
                  |//! The enclosing component
                  |$componentClassName& m_component;"""
            )
          )
        )
      guardedList (hasActionsOrGuards) (members)
    }

    private def writeComponentArgsWithTypeOpt[T](typeOpt: Option[T]): String = {
      object Utils extends StateMachineCppWriterUtils(s, smSymbol.node)
      val baseArgs = Utils.writeArgsWithValueOpt("signal", Some("value"), typeOpt)
      s"this->getId(), $baseArgs"
    }

  }

}

object ComponentInternalStateMachines {

  val hookParams = List(
    CppDoc.Function.Param(
      CppDoc.Type("SmId"),
      "smId",
      Some("The state machine ID")
    ),
    CppDoc.Function.Param(
      CppDoc.Type("FwEnumStoreType"),
      "signal",
      Some("The signal")
    ),
    CppDoc.Function.Param(
      CppDoc.Type("Fw::SerializeBufferBase&"),
      "buffer",
      Some("The message buffer")
    )
  )

}
