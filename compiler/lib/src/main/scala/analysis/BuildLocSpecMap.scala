package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Build the location specifier map 
 *  Prerequisites: Input file set */
object BuildLocSpecMap extends AstStateVisitor {

  type State = Analysis

  override def defModuleAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val Ast.DefModule(name, members) = node1.getData
    val a1 = a.copy(moduleNameList = name :: a.moduleNameList)
    for { a2 <- visitList(a1, members, matchModuleMember) }
    yield a2.copy(moduleNameList = a.moduleNameList)
  }

  override def specLocAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.SpecLoc]]
  ) = {
    System.out.println(s"BuildLocSpecMap: visiting ${node}")
    val specNode = node._2
    val spec = specNode.getData
    val symbolNode = spec.symbol
    val symbol = symbolNode.getData
    val qualifiedName = Name.Qualified.fromIdentList(symbol.reverse ++ a.moduleNameList)
    val loc = Locations.get(specNode.getId)
    for { 
      path <- loc.relativePath(spec.file) 
      a <- updateMap(a, qualifiedName, path.toString)
    } yield {
      a
    }
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) =
    visitList(a, tu.members, matchTuMember)

  private def updateMap(
    a: Analysis,
    qualifiedName: Name.Qualified,
    path: String
  ): Result = {
    default(a)
  }

}
