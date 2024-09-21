package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

/** Utilities for writing C++ state machines */
abstract class StateMachineCppWriterUtils(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
) extends CppWriterUtils {

  val node = aNode._2

  val data = node.data

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

  val astMembers = data.members.get

  val uninitStateName = "__FPRIME_AC_UNINITIALIZED"

  val leafStates = StateMachine.getLeafStates(symbol)

  def writeSmSymbolName(state: StateMachineSymbol) =
    CppWriterState.identFromQualifiedSmSymbolName(sma, state)

  val commentedLeafStateNames =
    leafStates.toList.map(
      state => {
        val comment = AnnotationCppWriter.asStringOpt(state).map(lines).
          getOrElse(Nil).map(CppDocWriter.addCommentPrefix ("//!") _)
        val name = writeSmSymbolName(StateMachineSymbol.State(state))
        (comment, name)
      }
    ).sortBy(_._2)

  val actionSymbols =
    StateMachine.getActions(aNode._2.data).map(StateMachineSymbol.Action(_))

  val actionParamName = "__fprime_ac_param"

  def getActionFnName(sym: StateMachineSymbol.Action): String =
    s"action_${sym.getUnqualifiedName}"

  def writeActionCall (paramNameOpt: Option[String]) (sym: StateMachineSymbol.Action) = {
    val paramString = (paramNameOpt, sym.node._2.data.typeName) match {
      case (Some(paramName), Some(_)) => paramName
      case _ => ""
    }
    val actionFnName = getActionFnName(sym)
    lines(s"this->$actionFnName($paramString);")
  }

  val writeNoArgActionCall = writeActionCall ((None))

  def getEnterFunctionName(soj: StateOrJunction) =
    s"enter_${writeSmSymbolName(soj.getSymbol)}"

  def writeStateUpdate(sym: StateMachineSymbol.State) = {
    val stateName = writeSmSymbolName(sym)
    lines(s"this->m_state = State::$stateName;")
  }

}
