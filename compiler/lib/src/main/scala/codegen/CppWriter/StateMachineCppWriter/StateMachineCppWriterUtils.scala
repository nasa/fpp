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

  val leafStates = StateMachine.getLeafStates(symbol)

  val actionSymbols =
    StateMachine.getActions(aNode._2.data).map(StateMachineSymbol.Action(_))

  val guardSymbols =
    StateMachine.getGuards(aNode._2.data).map(StateMachineSymbol.Guard(_))

  val valueParamName = "value"

  def getActionFunctionName(sym: StateMachineSymbol.Action): String =
    s"action_${sym.getUnqualifiedName}"

  def getGuardFunctionName(sym: StateMachineSymbol.Guard): String =
    s"guard_${sym.getUnqualifiedName}"

  val signalSymbols =
    StateMachine.getSignals(aNode._2.data).map(StateMachineSymbol.Signal(_))

  val signalParamName = "signal"

  val signalParam = CppDoc.Function.Param(
    CppDoc.Type("Signal"),
    signalParamName,
    Some("The signal")
  )

  def commentedLeafStateNames =
    leafStates.toList.map(
      state => (
        AnnotationCppWriter.writePreComment(state),
        writeSmSymbolName(StateMachineSymbol.State(state))
      )
    ).sortBy(_._2)

  def commentedSignalNames =
    signalSymbols.map(
      symbol => (
        AnnotationCppWriter.writePreComment(symbol.node),
        writeSmSymbolName(symbol)
      )
    ).sortBy(_._2)

  def getEnterFunctionName(symbol: StateMachineSymbol) =
    s"enter_${writeSmSymbolName(symbol)}"

  def writeActionCall (signalArg: String) (valueArgOpt: Option[String]) (sym: StateMachineSymbol.Action) = {
    val functionName = getActionFunctionName(sym)
    val args = writeArgsWithValueOpt(signalArg, valueArgOpt, sym.node._2.data.typeName)
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

  def writeEnterCall (signalArg: String) (valueArgOpt: Option[String]) (sym: StateMachineSymbol) = {
    val functionName = getEnterFunctionName(sym)
    val typeOpt = sym match {
      case StateMachineSymbol.Junction(aNode) =>
        val te = StateMachineTypedElement.Junction(aNode)
        sma.typeOptionMap(te)
      case _ => None
    }
    val args = writeArgsWithValueOpt(signalArg, valueArgOpt, typeOpt)
    lines(s"this->$functionName($args);")
  }

  def writeGuardCall (signalArg: String) (valueArgOpt: Option[String]) (sym: StateMachineSymbol.Guard) = {
    val functionName = getGuardFunctionName(sym)
    val args = writeArgsWithValueOpt(signalArg, valueArgOpt, sym.node._2.data.typeName)
    lines(s"this->$functionName($args);")
  }

  def writeNoValueActionCall = (signalArg: String) => writeActionCall (signalArg) (None)

  def writeNoValueEnterCall = (signalArg: String) => writeEnterCall (signalArg) (None)

  def writeNoValueGuardCall = (signalArg: String) => writeGuardCall (signalArg) (None)

  def getParamsWithTypeNameOpt(typeNameOpt: Option[AstNode[Ast.TypeName]]) =
    getParamsWithTypeOpt(typeNameOpt.map(tn => s.a.typeMap(tn.id)))

  def getParamsWithTypeOpt(typeOpt: Option[Type]) = {
    val valueArgs = typeOpt match {
      case Some(t) =>
        List(
          CppDoc.Function.Param(
            CppDoc.Type(typeCppWriter.write(t)),
            valueParamName,
            Some("The value")
          )
        )
      case None => Nil
    }
    signalParam :: valueArgs
  }

  def writeSmSymbolName(state: StateMachineSymbol) =
    CppWriterState.identFromQualifiedSmSymbolName(sma, state)

  def writeStateUpdate(sym: StateMachineSymbol.State) = {
    val stateName = writeSmSymbolName(sym)
    lines(s"this->m_state = State::$stateName;")
  }

  def writeTransition(
    transition: Transition,
    valueArgOpt: Option[String]
  ) = transition match {
    case Transition.External(actions, target) => lines("// TODO")
    case Transition.Internal(actions) => lines("// TODO")
  }

}
