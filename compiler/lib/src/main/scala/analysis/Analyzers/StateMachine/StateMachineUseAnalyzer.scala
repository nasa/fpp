package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Analyze state machine uses */
trait StateMachineUseAnalyzer
  extends StateMachineAnalysisVisitor
  with StateAnalyzer
{

  /** A use of an action definition */
  def actionUse(sma: StateMachineAnalysis, node: AstNode[Ast.Ident], use: Name.Unqualified): Result = default(sma)

  /** A use of a guard definition */
  def guardUse(sma: StateMachineAnalysis, node: AstNode[Ast.Ident], use: Name.Unqualified): Result = default(sma)

  /** A use of a signal definition */
  def signalUse(sma: StateMachineAnalysis, node: AstNode[Ast.Ident], use: Name.Unqualified): Result = default(sma)

  /** A use of a state definition or junction definition */
  def stateOrJunctionUse(sma: StateMachineAnalysis, node: AstNode[Ast.QualIdent], use: Name.Qualified): Result = default(sma)

  override def defJunctionAnnotatedNode(sma: StateMachineAnalysis, node: Ast.Annotated[AstNode[Ast.DefJunction]]) =
    // TODO
    default(sma)

  override def specInitialAnnotatedNode(sma: StateMachineAnalysis, node: Ast.Annotated[AstNode[Ast.SpecInitial]]) =
    transitionExpr(sma, node._2.data.transitionExpr)

  override def specTransitionAnnotatedNode(sma: StateMachineAnalysis, node: Ast.Annotated[AstNode[Ast.SpecTransition]]) =
    // TODO
    default(sma)

  private def transitionExpr(sma: StateMachineAnalysis, e: Ast.TransitionExpr): Result = {
    for {
      sma <- Result.foldLeft (e.actions) (sma) (identNode(actionUse))
      sma <- qualIdentNode(stateOrJunctionUse)(sma, e.destination)
    }
    yield sma
  }

  private def identNode
    (f: (StateMachineAnalysis, AstNode[Ast.Ident], Name.Unqualified) => Result) 
    (sma: StateMachineAnalysis, ident: AstNode[Ast.Ident]): Result = {
    f(sma, ident, ident.data)
  }

  private def qualIdentNode
    (f: (StateMachineAnalysis, AstNode[Ast.QualIdent], Name.Qualified) => Result) 
    (sma: StateMachineAnalysis, qualIdent: AstNode[Ast.QualIdent]): Result = {
    val use = Name.Qualified.fromQualIdent(qualIdent.data)
    f(sma, qualIdent, use)
  }

}
