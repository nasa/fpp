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
  ) =
    for {
      ci <- ComponentInstance.fromDefComponentInstance(
        a,
        aNode
      )
      a <- visitList(
        a.copy(componentInstance = Some(ci)),
        aNode._2.data.initSpecs,
        specInitAnnotatedNode
      )
    }
    yield {
      val symbol = Symbol.ComponentInstance(aNode)
      val map = a.componentInstanceMap + (symbol -> a.componentInstance.get)
      a.copy(componentInstanceMap = map)
    }

  override def specInitAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInit]]
  ) =
    for {
      is <- InitSpecifier.fromNode(a, aNode)
      ci <- a.componentInstance.get.addInitSpecifier(is)
    }
    yield {
      a.copy(componentInstance = Some(ci))
    }

  /** Ensure that ID ranges do not overlap */
  def checkIdRanges(a: Analysis): Result.Result[Unit] = {
    val instances = a.componentInstanceMap.values.toList.
      sortWith(_.baseId < _.baseId)
    def check(instances: List[ComponentInstance]): Result.Result[Unit] =
      instances match {
        case (i1 :: i2 :: tail) =>
          if (i2.maxId >= i2.baseId && i1.baseId >= i2.baseId) 
            // Error: i2 has a nonempty ID range, and
            // base id for i1 lies in the ID range of i2
            Left(
              SemanticError.OverlappingIdRanges(
                i1.baseId,
                i1.aNode._2.data.name,
                Locations.get(i1.aNode._2.id),
                i2.baseId,
                i2.maxId,
                i2.aNode._2.data.name,
                Locations.get(i2.aNode._2.id)
              )
            )
          else if (i1.maxId < i2.baseId)
            // OK: ID ranges do not overlap; continue checking
            check(i2 :: tail)
          else Left(
            // Error: Base id for i2 lies in the ID range of i1
            SemanticError.OverlappingIdRanges(
              i2.baseId,
              i2.aNode._2.data.name,
              Locations.get(i1.aNode._2.id),
              i1.baseId,
              i1.maxId,
              i1.aNode._2.data.name,
              Locations.get(i1.aNode._2.id)
            )
          )
        case _ => Right(())
      }
    for {
      _ <- check(instances)
    } yield ()
  }

}
