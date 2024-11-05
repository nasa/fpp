package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze state machine uses */
trait StateMachineUseAnalyzer
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

  // ----------------------------------------------------------------------
  // Interface methods to override
  // Each of these methods is called when a corresponding use occurs
  // ----------------------------------------------------------------------

  /** A use of an action definition */
  def actionUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident],
    use: Name.Unqualified
  ): Result = default(sma)

  /** A use of a guard definition */
  def guardUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident],
    use: Name.Unqualified
  ): Result = default(sma)

  /** A use of a signal definition */
  def signalUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.Ident],
    use: Name.Unqualified
  ): Result = default(sma)

  /** A use of a state definition or choice definition */
  def stateOrChoiceUse(
    sma: StateMachineAnalysis,
    node: AstNode[Ast.QualIdent],
    use: Name.Qualified
  ): Result = default(sma)

  // ----------------------------------------------------------------------
  // Implementation using StateMachineAnalysisVisitor
  // ----------------------------------------------------------------------

  override def defChoiceAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.DefChoice]]
  ) = {
    val data = aNode._2.data
    for {
      sma <- identNode(guardUse)(sma, data.guard)
      sma <- transitionExpr(sma, data.ifTransition.data)
      sma <- transitionExpr(sma, data.elseTransition.data)
    }
    yield sma
  }

  override def specStateEntryAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateEntry]]
  ) = actions(sma, aNode._2.data.actions)

  override def specStateExitAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateExit]]
  ) = actions(sma, aNode._2.data.actions)

  override def specInitialTransitionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInitialTransition]]
  ) = transitionExpr(sma, aNode._2.data.transition.data)

  override def specStateTransitionAnnotatedNode(
    sma: StateMachineAnalysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecStateTransition]]
  ) = {
    val data = aNode._2.data
    for {
      sma <- identNode(signalUse)(sma, data.signal)
      sma <- opt(identNode(guardUse))(sma, data.guard)
      sma <- transitionOrDo(sma, data.transitionOrDo)
    }
    yield sma
  }

  // ----------------------------------------------------------------------
  // Private helper methods
  // ----------------------------------------------------------------------

  private def transitionExpr(
    sma: StateMachineAnalysis,
    e: Ast.TransitionExpr
  ): Result =
    for {
      sma <- actions(sma, e.actions)
      sma <- qualIdentNode(stateOrChoiceUse)(sma, e.target)
    }
    yield sma

  private def actions(sma: StateMachineAnalysis, actions: List[AstNode[Ast.Ident]]) =
    Result.foldLeft (actions) (sma) (identNode(actionUse))

  private def transitionOrDo(
    sma: StateMachineAnalysis,
    tod: Ast.TransitionOrDo
  ): Result = tod match {
    case Ast.TransitionOrDo.Transition(e) => transitionExpr(sma, e.data)
    case Ast.TransitionOrDo.Do(as) => actions(sma, as)
  }

  private def identNode
    (f: (StateMachineAnalysis, AstNode[Ast.Ident], Name.Unqualified) => Result)
    (sma: StateMachineAnalysis, ident: AstNode[Ast.Ident]): Result =
    f(sma, ident, ident.data)

  private def qualIdentNode
    (f: (StateMachineAnalysis, AstNode[Ast.QualIdent], Name.Qualified) => Result)
    (sma: StateMachineAnalysis, qualIdent: AstNode[Ast.QualIdent]): Result = {
    val use = Name.Qualified.fromQualIdent(qualIdent.data)
    f(sma, qualIdent, use)
  }

}
