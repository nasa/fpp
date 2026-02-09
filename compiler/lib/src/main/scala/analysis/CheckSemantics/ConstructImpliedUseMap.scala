package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct the implied use map */
object ConstructImpliedUseMap
  extends Analyzer
  with ModuleAnalyzer
{

  override def defStateMachineAnnotatedNodeInternal(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) = {
    val id = aNode._2.id
    val sym = Symbol.StateMachine(aNode)
    val qualifier = a.getQualifiedName(sym)
    val name = Name.Qualified(qualifier.toIdentList, "State")
    // TODO
    Right(a)
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val id = aNode._2.id
    val typeNames = ImpliedUse.getTopologyTypes(a)
    val empty: ImpliedUse.Uses = Map()
    val typeMap = typeNames.foldLeft (empty) ((m, tn) => {
      val id1 = ImpliedUse.replicateId(id)
      val impliedUse = ImpliedUse.fromIdentListAndId(tn, id1)
      val set = m.get(ImpliedUse.Kind.Type).getOrElse(Set())
      m + (ImpliedUse.Kind.Type -> (set + impliedUse))
    })

    val constants = ImpliedUse.getTopologyConstants(a)
    val map = constants.foldLeft (typeMap) ((m, c) => {
      val id1 = ImpliedUse.replicateId(id)
      val impliedUse = ImpliedUse.fromIdentListAndId(c, id1)
      val set = m.get(ImpliedUse.Kind.Constant).getOrElse(Set())
      m + (ImpliedUse.Kind.Constant -> (set + impliedUse))
    })
    Right(a.copy(impliedUseMap = a.impliedUseMap + (id -> map)))
  }

}
