package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check component instance definitions */
object CheckComponentInstanceDefs
  extends Analyzer 
  with ModuleAnalyzer
{

  override def defComponentInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ) = for {
    componentInstance <- ComponentInstances.fromDefComponentInstance(
      a,
      aNode
    )
  }
  yield {
    val symbol = Symbol.ComponentInstance(aNode)
    val map = a.componentInstanceMap + (symbol -> componentInstance)
    a.copy(componentInstanceMap = map)
  }

  /** Ensure that ID ranges do not overlap */
  def checkIdRanges(a: Analysis): Result.Result[Unit] = {
    val instances = a.componentInstanceMap.values.toList.
      sortWith(_.baseId < _.baseId)
    def check(instances: List[ComponentInstance]): Result.Result[Unit] =
      instances match {
        case (i1 :: i2 :: tail) =>
          if (i1.maxId < i2.baseId) check(i2 :: tail)
          else Left(
            SemanticError.OverlappingIdRanges(
              i1.maxId,
              i1.aNode._2.data.name,
              Locations.get(i1.aNode._2.id),
              i2.baseId,
              i2.aNode._2.data.name,
              Locations.get(i2.aNode._2.id)
            )
          )
        case _ => Right(())
      }
    for {
      _ <- check(instances)
    } yield ()
  }

}
