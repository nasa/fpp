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

  val commentedLeafStateNames =
    leafStates.toList.map(
      state => {
        val comment = AnnotationCppWriter.asStringOpt(state).map(lines).
          getOrElse(Nil).map(CppDocWriter.addCommentPrefix ("//!") _)
        val ident = CppWriterState.identFromQualifiedSmSymbolName(
          stateMachine.sma,
          StateMachineSymbol.State(state)
        )
        (comment, ident)
      }
    ).sortBy(_._2)

  val actionSymbols =
    StateMachine.getActions(aNode._2.data).map(StateMachineSymbol.Action(_))

  val actionParamName = "__fprime_ac_param"


}
