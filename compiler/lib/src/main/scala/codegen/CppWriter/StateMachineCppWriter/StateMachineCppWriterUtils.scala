package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Utilities for writing C++ state machines */
abstract class StateMachineCppWriterUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
) extends CppWriterUtils {

  val symbol = Symbol.StateMachine(aNode)

  val stateMachine: StateMachine = s.a.stateMachineMap(symbol)

  val sma = stateMachine.sma

  val name = s.getName(symbol)

  val className = s"${name}StateMachineBase"

  val fileName = ComputeCppFiles.FileNames.getStateMachine(
    name,
    StateMachine.Kind.Internal
  )

  val namespaceIdentList = s.getNamespaceIdentList(symbol)

  val typeCppWriter = TypeCppWriter(s)

  val uninitStateName = "__FPRIME_AC_UNINITIALIZED"

  val initialTransitionName = "__FPRIME_AC_INITIAL_TRANSITION"

  val leafStateSymbols =
    StateMachine.getLeafStates(symbol).map(StateMachineSymbol.State(_)).
      toList.sortBy(writeSmSymbolName)

  val actionSymbols =
    StateMachine.getActions(aNode._2.data).map(StateMachineSymbol.Action(_))

  val guardSymbols =
    StateMachine.getGuards(aNode._2.data).map(StateMachineSymbol.Guard(_))

  val valueParamName = "value"

  def getActionFunctionName(sym: StateMachineSymbol.Action): String =
    s"action_${sym.getUnqualifiedName}"

  def getGuardFunctionName(sym: StateMachineSymbol.Guard): String =
    s"guard_${sym.getUnqualifiedName}"

  def getSendSignalFunctionName(sym: StateMachineSymbol.Signal): String =
    s"sendSignal_${sym.getUnqualifiedName}"

  val signalSymbols =
    StateMachine.getSignals(aNode._2.data).map(StateMachineSymbol.Signal(_))

  val signalParamName = "signal"

  val signalParam = CppDoc.Function.Param(
    CppDoc.Type("Signal"),
    signalParamName,
    Some("The signal")
  )

  def commentedLeafStateNames =
    leafStateSymbols.toList.map(
      state => (
        AnnotationCppWriter.writePreComment(state.node),
        writeSmSymbolName(state)
      )
    ).sortBy(_._2)

  def commentedSignalNames =
    signalSymbols.map(
      symbol => (
        AnnotationCppWriter.writePreComment(symbol.node),
        writeSmSymbolName(symbol)
      )
    ).sortBy(_._2)

  def getActionFunctionParams(sym: StateMachineSymbol.Action):
  List[CppDoc.Function.Param] =
    getParamsWithTypeNameOpt(sym.node._2.data.typeName)

  def getEnterFunctionName(symbol: StateMachineSymbol) =
    s"enter_${writeSmSymbolName(symbol)}"

  def getGuardFunctionParams(sym: StateMachineSymbol.Guard):
  List[CppDoc.Function.Param] =
    getParamsWithTypeNameOpt(sym.node._2.data.typeName)

  def getParamsWithTypeNameOpt(typeNameOpt: Option[AstNode[Ast.TypeName]]) =
    getParamsWithTypeOpt(typeNameOpt.map(tn => s.a.typeMap(tn.id)))

  def getParamsWithTypeOpt(typeOpt: Option[Type]) =
    signalParam :: getValueParamsWithTypeOpt(typeOpt)

  def getValueParamType(t: Type): CppDoc.Type = {
    val typeName = typeCppWriter.write(t)
    val paramTypeName = if t.isPrimitive
                        then typeName
                        else s"const $typeName&"
    CppDoc.Type(paramTypeName)
  }

  def getValueParamsWithTypeNameOpt(typeNameOpt: Option[AstNode[Ast.TypeName]]) =
    getValueParamsWithTypeOpt(typeNameOpt.map(tn => s.a.typeMap(tn.id)))

  def getValueParamsWithTypeOpt(typeOpt: Option[Type]) = typeOpt match {
    case Some(t) =>
      List(
        CppDoc.Function.Param(
          getValueParamType(t),
          valueParamName,
          Some("The value")
        )
      )
    case None => Nil
  }

  def writeActionCall
    (signalArg: String)
    (valueArgOpt: Option[String])
    (sym: StateMachineSymbol.Action): List[Line] =
  {
    val functionName = getActionFunctionName(sym)
    val args = writeArgsWithValueOpt(
      signalArg,
      valueArgOpt,
      sym.node._2.data.typeName
    )
    lines(s"this->$functionName($args);")
  }

  def writeArgsWithValueOpt[T](
    signalArg: String,
    valueArgOpt: Option[String],
    typeOpt: Option[T]
  ) = (valueArgOpt, typeOpt) match {
    case (Some(valueArg), Some(_)) => s"$signalArg, $valueArg"
    case _ => signalArg
  }
  def writeEnterCall
    (signalArg: String)
    (valueArgOpt: Option[String])
    (sym: StateMachineSymbol): List[Line] =
  {
    val functionName = getEnterFunctionName(sym)
    val typeOpt = sym match {
      case StateMachineSymbol.Choice(aNode) =>
        val te = StateMachineTypedElement.Choice(aNode)
        sma.typeOptionMap(te)
      case _ => None
    }
    val args = writeArgsWithValueOpt(signalArg, valueArgOpt, typeOpt)
    lines(s"this->$functionName($args);")
  }

  def writeGuardCall
    (signalArg: String)
    (valueArgOpt: Option[String])
    (sym: StateMachineSymbol.Guard): String =
  {
    val functionName = getGuardFunctionName(sym)
    val args = writeArgsWithValueOpt(
      signalArg,
      valueArgOpt,
      sym.node._2.data.typeName
    )
    s"this->$functionName($args)"
  }

  def writeGuardedTransition
    (signal: StateMachineSymbol.Signal)
    (gt: Transition.Guarded): List[Line] =
  {
    val signalArg = writeSignalName(signal)
    val valueArgOpt = signal.node._2.data.typeName.map(_ => valueParamName)
    val transitionLines =
      writeTransition (signalArg) (valueArgOpt) (gt.transition)
    gt.guardOpt match {
      case Some(guard) =>
        val guardCall = writeGuardCall (signalArg) (valueArgOpt) (guard)
        wrapInIf(guardCall, transitionLines)
      case None => transitionLines
    }
  }

  def writeSignalName(signal: StateMachineSymbol.Signal) =
    s"Signal::${writeSmSymbolName(signal)}"

  def writeSmSymbolName(state: StateMachineSymbol) =
    CppWriterState.identFromQualifiedSmSymbolName(sma, state)

  def writeStateName(state: StateMachineSymbol.State) =
    s"State::${writeSmSymbolName(state)}"

  def writeStateUpdate(sym: StateMachineSymbol.State) = {
    val stateName = writeSmSymbolName(sym)
    lines(s"this->m_state = State::$stateName;")
  }

  def writeTransition
    (signalArg: String)
    (valueArgOpt: Option[String])
    (transition: Transition): List[Line] =
  {
    val actionComment = line("// Do the actions for the transition")
    val actionLines = transition.getActions.flatMap(
      writeActionCall (signalArg) (valueArgOpt)
    )
    val entryComment = line("// Enter the target")
    val entryLines = transition.getTargetOpt match {
      case Some(target) =>
        writeEnterCall (signalArg) (valueArgOpt) (target.getSymbol)
      case None => Nil
    }
    List.concat(
      Line.addPrefixLine (actionComment) (actionLines),
      Line.addPrefixLine (entryComment) (entryLines)
    )
  }

}
