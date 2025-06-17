package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct the implied use map */
object ConstructImpliedUseMap
  extends Analyzer
  with ModuleAnalyzer
{

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val id = aNode._2.id
    val typeNames = ImpliedUse.getTopologyTypes(a)
    val empty: ImpliedUse.Uses = Map()
    val map = typeNames.foldLeft (empty) ((m, tn) => {
      val id1 = ImpliedUse.replicateId(id)
      val impliedUse = ImpliedUse.fromIdentListAndId(tn, id1)
      val set = m.get(ImpliedUse.Kind.Type).getOrElse(Set())
      m + (ImpliedUse.Kind.Type -> (set + impliedUse))
    })
    Right(a.copy(impliedUseMap = a.impliedUseMap + (id -> map)))
  }

}
