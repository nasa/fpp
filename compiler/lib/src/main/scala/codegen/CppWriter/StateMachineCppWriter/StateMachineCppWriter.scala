package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Writes out C++ for state machine definitions */
case class StateMachineCppWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
) extends StateMachineCppWriterUtils(s, aNode) {

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name state machine",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getActionFunctionParams(sym: StateMachineSymbol.Action):
  List[CppDoc.Function.Param] =
    getParamsWithTypeNameOpt(sym.node._2.data.typeName)

  private def getActionMember(sym: StateMachineSymbol.Action):
  CppDoc.Class.Member.Function =
    functionClassMember(
      AnnotationCppWriter.asStringOpt(sym.node),
      getActionFunctionName(sym),
      getActionFunctionParams(sym),
      CppDoc.Type("void"),
      Nil,
      CppDoc.Function.PureVirtual
    )

  private def getActionMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Actions",
      actionSymbols.map(getActionMember),
      CppDoc.Lines.Hpp
    )

  private def getClassMembers: List[CppDoc.Class.Member] =
    List.concat(
      getTypeMembers,
      getConstructorDestructorMembers,
      getInitMembers,
      getSendSignalMembers,
      getActionMembers,
      getGuardMembers,
      getEntryMembers,
      getVariableMembers
    )

  private def getConstructorDestructorMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Constructors and Destructors",
      List(
        constructorClassMember(
          Some("Constructor"),
          Nil,
          Nil,
          Nil
        ),
        destructorClassMember(
          Some("Destructor"),
          Nil,
          CppDoc.Class.Destructor.Virtual
        )
      )
    )

  private def getCppIncludes: CppDoc.Member = {
    val userHeaders = List.concat(
      standardUserCppHeaders,
      List(s"${s.getRelativePath(fileName)}.hpp").map(CppWriter.headerString)
    ).sorted
    val headerLines = List.concat(
      addBlankPrefix(standardSystemCppHeaders.map(line)),
      addBlankPrefix(userHeaders.map(line))
    )
    linesMember(headerLines, CppDoc.Lines.Cpp)
  }

  private def getEntryMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      "State and junction entry",
      StateMachineEntryFns(s, aNode).write
    )

  private def getEnumClassMember(
    comment: String,
    className: String,
    pairs: List[(List[Line], String)]
  ): CppDoc.Class.Member = {
    val enumLines = pairs.flatMap {
      case (comment, name) => List.concat(comment, lines(s"$name,"))
    }
    val memberLines = List.concat(
      CppDocWriter.writeDoxygenComment(comment),
      wrapInEnumClass(className, enumLines, Some("FwEnumStoreType"))
    )
    linesClassMember(memberLines)
  }

  private def getGuardFunctionParams(sym: StateMachineSymbol.Guard):
  List[CppDoc.Function.Param] =
    getParamsWithTypeNameOpt(sym.node._2.data.typeName)

  private def getGuardMember(sym: StateMachineSymbol.Guard):
  CppDoc.Class.Member.Function = {
    functionClassMember(
      AnnotationCppWriter.asStringOpt(sym.node),
      getGuardFunctionName(sym),
      getGuardFunctionParams(sym),
      CppDoc.Type("bool"),
      Nil,
      CppDoc.Function.PureVirtual,
      CppDoc.Function.Const
    )
  }

  private def getGuardMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Guards",
      guardSymbols.map(getGuardMember),
      CppDoc.Lines.Hpp
    )

  private def getGuardedTransitionLines (signal: StateMachineSymbol.Signal) (gt: Transition.Guarded):
  List[Line] = {
    val transitionLines = getTransitionLines(signal, gt.transition)
    gt.guardOpt match {
      case Some(guard) =>
        val valueArgOpt = guard.node._2.data.typeName.map(_ => valueParamName)
        val guardCall = writeGuardCall (writeSignalName(signal)) (valueArgOpt) (guard)
        wrapInIf(guardCall, transitionLines)
      case None => transitionLines
    }
  }

  private def getHppIncludes: CppDoc.Member = {
    val symbolHeaders = {
      val Right(a) = UsedSymbols.defStateMachineAnnotatedNode(s.a, aNode)
      s.writeIncludeDirectives(a.usedSymbolSet)
    }
    val userHeaders = List.concat(
      standardUserHppHeaders,
      symbolHeaders
    ).sorted
    val headerLines = List.concat(
      addBlankPrefix(standardSystemHppHeaders.map(line)),
      addBlankPrefix(userHeaders.map(line))
    )
    linesMember(headerLines)
  }

  private def getInitMember: CppDoc.Class.Member = {
    val initSpecifier = StateMachine.getInitialSpecifier(aNode._2.data)._2.data
    val transition = initSpecifier.transition.data
    val actionSymbols = transition.actions.map(sma.getActionSymbol)
    val targetSymbol = sma.useDefMap(transition.target.id)
    val signal = s"Signal::$initialTransitionName"
    functionClassMember(
      Some("Initialize the state machine"),
      "init",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("const FwEnumStoreType"),
          "id",
          Some("The state machine ID")
        )
      ),
      CppDoc.Type("void"),
      line("this->m_id = id;") :: List.concat(
        actionSymbols.flatMap(writeNoValueActionCall (signal)),
        writeNoValueEnterCall (signal) (targetSymbol)
      )
    )
  }

  private def getInitMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      "Initialization",
      List(getInitMember)
    )

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = classMember(
      AnnotationCppWriter.asStringOpt(aNode),
      className,
      None,
      getClassMembers
    )
    hppIncludes :: cppIncludes :: wrapInNamespaces(namespaceIdentList, List(cls))
  }

  private def getSendSignalFunctionParams(sym: StateMachineSymbol.Signal):
  List[CppDoc.Function.Param] =
    getValueParamsWithTypeNameOpt(sym.node._2.data.typeName)

  private def getSendSignalMember(sym: StateMachineSymbol.Signal):
  CppDoc.Class.Member.Function = functionClassMember(
    AnnotationCppWriter.asStringOpt(sym.node),
    getSendSignalFunctionName(sym),
    getSendSignalFunctionParams(sym),
    CppDoc.Type("void"),
    getSendSignalMemberLines(sym)
  )

  private def getSendSignalMemberCaseLines (signal: StateMachineSymbol.Signal) (state: StateMachineSymbol.State):
  List[Line] = {
    line( s"case ${writeStateName(state)}:") ::
    List.concat(
      sma.flattenedStateTransitionMap.get(signal).getOrElse(Map()).
        get(state).map(getGuardedTransitionLines (signal)).getOrElse(Nil),
      lines("break;")
    ).map(indentIn)
  }

  private def getSendSignalMemberLines(signal: StateMachineSymbol.Signal):
  List[Line] = wrapInSwitch(
    "this->m_state",
    List.concat(
      leafStateSymbols.flatMap(getSendSignalMemberCaseLines (signal)),
      lines(
        """|default:
           |  FW_ASSERT(0, static_cast<FwAssertArgType>(this->m_state));
           |  break;"""
      )
    )
  )

  private def getSendSignalMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      "Send signal functions",
      signalSymbols.map(getSendSignalMember)
    )

  private def getSignalEnumClassMember: CppDoc.Class.Member = {
    val initialPair = (lines("//! The initial transition"), initialTransitionName)
    val pairs = initialPair :: commentedSignalNames
    getEnumClassMember("The signal type", "Signal", pairs)
  }

  private def getStateEnumClassMember: CppDoc.Class.Member = {
    val initialPair = (lines("//! The uninitialized state"), uninitStateName)
    val pairs = initialPair :: commentedLeafStateNames
    getEnumClassMember("The state type", "State", pairs)
  }

  private def getTransitionLines(signal: StateMachineSymbol.Signal, transition: Transition):
  List[Line] =  {
    val signalArg = writeSignalName(signal)
    val valueArgOpt = signal.node._2.data.typeName.map(_ => valueParamName)
    def writeActions(actions: List[StateMachineSymbol.Action]) =
      actions.flatMap(writeActionCall (signalArg) (valueArgOpt))
    transition match {
      case Transition.External(actions, target) =>
        List.concat(
          writeActions(actions),
          writeEnterCall (signalArg) (valueArgOpt) (target.getSymbol)
        )
      case Transition.Internal(actions) =>
        writeActions(actions)
    }
  }

  private def getTypeMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Types",
      List(
        getStateEnumClassMember,
        getSignalEnumClassMember
      ),
      CppDoc.Lines.Hpp
    )

  private def getVariableMembers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PROTECTED",
      "Member variables",
      List(
        linesClassMember(
          Line.blank ::
          lines(
            s"""|//! The state machine ID
                |FwEnumStoreType m_id = 0;
                |
                |//! The state
                |State m_state = State::$uninitStateName;"""
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

}
