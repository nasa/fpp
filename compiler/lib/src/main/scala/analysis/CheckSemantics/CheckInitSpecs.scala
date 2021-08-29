package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check init specifiers */
object CheckInitSpecs
  extends Analyzer 
  with ModuleAnalyzer
{

  override def defComponentInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ) = {
    val cis = Symbol.ComponentInstance(aNode)
    val ci = a.componentInstanceMap(cis)
    val a1 = a.copy(componentInstance = Some(ci))
    visitList(a1, aNode._2.data.initSpecs, specInitAnnotatedNode)
  }

  override def specInitAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecInit]]
  ) = for {
    is <- InitSpecifier.fromNode(a, a.componentInstance.get, aNode)
    map <- {
      val map1 = a.initSpecifierMap.getOrElse(is.instance, Map())
      map1.get(is.phase) match {
        case Some(prevIs) =>
          val loc = is.getLoc
          val prevLoc = prevIs.getLoc
          Left(
            SemanticError.DuplicateInitSpecifier(
              is.phase,
              loc,
              prevLoc
            )
          )
        case None => {
          val map2 = map1 + (is.phase -> is)
          Right(a.initSpecifierMap + (is.instance -> map2))
        }
      }
    }
  }
  yield a.copy(initSpecifierMap = map)

}
