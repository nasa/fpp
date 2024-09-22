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
  CppDoc.Class.Member.Function = {
    functionClassMember(
      AnnotationCppWriter.asStringOpt(sym.node),
      getActionFunctionName(sym),
      getActionFunctionParams(sym),
      CppDoc.Type("void"),
      Nil,
      CppDoc.Function.PureVirtual
    )
  }

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

  private def getInitMembers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "public",
      "Initialization",
      List(getInitMember)
    )
  }

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
