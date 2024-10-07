package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

case class ComponentInternalStateMachines(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val componentClassName = className

  /** Gets the function members */
  def getFunctionMembers: List[CppDoc.Class.Member] = List.concat(
    getSignalSendFunctions,
    getOverflowHooks,
    getVirtualActions,
    getVirtualGuards
  )

  /** Gets the type members */
  def getTypeMembers: List[CppDoc.Class.Member] =
    List.concat(
      addAccessTagAndComment(
        "public",
        "Public types for internal state machines",
        SignalBufferWriter.getSignalBuffer
      ),
      addAccessTagAndComment(
        "PROTECTED",
        "Protected types for internal state machines",
        getStateMachines
      )
    )

  /** Writes the dispatch case, if any, for internal state machine instances */
  def writeDispatchCase: List[Line] =
    // TODO
    Nil

  private val stateMachineIdParam = CppDoc.Function.Param(
    CppDoc.Type("SmId"),
    "smId",
    Some("The state machine id")
  )

  private def getBufferSizeConstant: List[CppDoc.Class.Member] = {
    lazy val member = linesClassMember(
      List.concat(
        CppDocWriter.writeDoxygenComment("The state machine signal buffer size"),
        lines("static constexpr FwSizeType SM_SIGNAL_BUFFER_SIZE = SmSignalBuffer::SERIALIZED_SIZE;")
      )
    )
    addAccessTagAndComment(
      "public",
      "Buffer size for internal state machines",
      guardedList (hasInternalStateMachineInstances) (List(member))
    )
  }

  private def getComponentActionFunctionName(
    sm: Symbol.StateMachine,
    action: StateMachineSymbol.Action
  ): String = {
    object Utils extends StateMachineCppWriterUtils(s, sm.node)
    val implName = writeStateMachineImplType(sm)
    val baseName = Utils.getActionFunctionName(action)
    s"${implName}_$baseName"
  }

  private def getComponentActionFunctionParams(
    sm: Symbol.StateMachine,
    action: StateMachineSymbol.Action
  ) = {
    object Utils extends StateMachineCppWriterUtils(s, sm.node)
    val smName = writeStateMachineImplType(sm)
    val signalParam = CppDoc.Function.Param(
      CppDoc.Type(s"${smName}::Signal"),
      Utils.signalParamName,
      Some("The signal")
    )
    val valueParams = Utils.getValueParamsWithTypeNameOpt(action.node._2.data.typeName)
    stateMachineIdParam :: signalParam :: valueParams
  }

  private def getOverflowHooks: List[CppDoc.Class.Member] =
    // TODO
    Nil

  private def getSignalSendFunctions: List[CppDoc.Class.Member] =
    // TODO
    Nil

  private def getStateMachines: List[CppDoc.Class.Member] =
    internalSmSymbols.map(
      symbol => StateMachineWriter(symbol).getStateMachine
    )

  private def getVirtualActions: List[CppDoc.Class.Member] = {
    val functionMembers = internalSmSymbols.flatMap(
      smSymbol => {
        val sm = s.a.stateMachineMap(smSymbol)
        sm.actions.map(
          action => {
            val smName = writeStateMachineImplType(sm.getSymbol)
            val actionName = action.getUnqualifiedName
            functionClassMember(
              Some(s"Implementation for action $actionName of state machine $smName"),
              getComponentActionFunctionName(smSymbol, action),
              getComponentActionFunctionParams(smSymbol, action),
              CppDoc.Type("void"),
              Nil,
              CppDoc.Function.PureVirtual
            )
          }
        )
      }
    )
    addAccessTagAndComment(
      "PROTECTED",
      "Functions to implement for internal state machine actions",
      functionMembers
    )
  }

  private def getVirtualGuards: List[CppDoc.Class.Member] =
    // TODO
    Nil

  private object SignalBufferWriter extends ComponentCppWriterUtils(s, aNode) {

    def getSignalBuffer: List[CppDoc.Class.Member] =
      guardedList (hasInternalStateMachineInstances) (
        List(
          classClassMember(
            Some("Buffer for serializing internal state machine signals"),
            "SmSignalBuffer",
            Some("public Fw::SerializeBufferBase"),
            List.concat(
              getTypeMembers,
              getConstantMembers,
              getFunctionMembers,
              getVariableMembers
            )
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

    private def getConstantMembers: List[CppDoc.Class.Member] =
      linesClassMember(CppDocHppWriter.writeAccessTag("public")) ::
      List(getSerializedSizeConstant)

    private def getFunctionMembers: List[CppDoc.Class.Member] =
      linesClassMember(CppDocHppWriter.writeAccessTag("public")) ::
      List(
        linesClassMember(
          lines(
            """|
               |//! Get the buffer capacity
               |Fw::Serializable::SizeType getBuffCapacity() const {
               |  return sizeof(this->m_buff);
               |}
               |
               |//! Get the buffer address (non-const)
               |U8* getBuffAddr() {
               |  return this->m_buff;
               |}
               |
               |//! Get the buffer address (const)
               |const U8* getBuffAddr() const {
               |  return this->m_buff;
               |}"""
          )
        )
      )

    private def getSerializedSizeConstant: CppDoc.Class.Member = {
      val comment = CppDocWriter.writeDoxygenComment(
        "The serialized size"
      )
      val terms = "2 * sizeof(FwEnumStoreType)" ::
        (guardedList (hasSignalTypes) (List("sizeof(SignalTypeUnion)")))
      val sum = s"${terms.mkString(" +\n")};"
      val constantLines = line("static constexpr FwSizeType SERIALIZED_SIZE =") ::
        lines(sum).map(indentIn)
      linesClassMember(List.concat(comment, constantLines))
    }

    private def getSignalTypeUnion: CppDoc.Class.Member = {
      val members = signalTypes.map (
        t => {
          val cppType = writeSignalTypeName(t)
          val typeIdent = cppType.replaceAll("::", "_")
          val sizeIdent = s"size_of_$typeIdent"
          val sizeExpr = writeSignalTypeSize(t)
          line(s"BYTE $sizeIdent[$sizeExpr];")
        }
      )
      val comment = CppDocWriter.writeDoxygenComment(
        "The union of the signal types, for sizing"
      )
      val union = wrapInScope("union SignalTypeUnion {", members, "};")
      linesClassMember(List.concat(comment, union))
    }

    private def getTypeMembers: List[CppDoc.Class.Member] =
      guardedList (hasSignalTypes) (
        linesClassMember(CppDocHppWriter.writeAccessTag("private")) ::
        List(getSignalTypeUnion)
      )

    private def getVariableMembers: List[CppDoc.Class.Member] =
      linesClassMember(CppDocHppWriter.writeAccessTag("private")) ::
      List(
        linesClassMember(
          lines(
            """|
               |//! The buffer
               |U8 m_buff[SERIALIZED_SIZE];"""
          )
        )
      )

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

  private case class StateMachineWriter(smSymbol: Symbol.StateMachine)
    extends ComponentCppWriterUtils(s, aNode)
  {

    val stateMachine = s.a.stateMachineMap(smSymbol)

    val hasActionsOrGuards = stateMachine.hasActions || stateMachine.hasGuards

    def getStateMachine: CppDoc.Class.Member = {
      val smClassName = writeStateMachineImplType(smSymbol)
      val baseClassName = s"${s.writeSymbol(smSymbol)}StateMachineBase"
      classClassMember(
        Some(s"Implementation for state machine ${smClassName}"),
        smClassName,
        Some(s"public $baseClassName"),
        List.concat(
          getConstructorMembers,
          getInitMembers,
          getActionMembers,
          getGuardMembers,
          getVariableMembers
        )
      )
    }

    private def getConstructorMembers: List[CppDoc.Class.Member] = Nil

    private def getInitMembers: List[CppDoc.Class.Member] = Nil

    private def getActionMembers: List[CppDoc.Class.Member] = Nil

    private def getGuardMembers: List[CppDoc.Class.Member] = Nil

    private def getVariableMembers: List[CppDoc.Class.Member] = {
      lazy val members =
        linesClassMember(CppDocHppWriter.writeAccessTag("private")) ::
        List(
          linesClassMember(
            lines(
              s"""|
                  |//! The enclosing component
                  |$componentClassName& component;"""
            )
          )
        )
      guardedList (hasActionsOrGuards) (members)
    }

  }

}
