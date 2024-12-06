package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** ====================================================================== 
 *  Compute the names of the layout directories to generate
 *  ====================================================================== 
 *  Check for duplicates that would cause a name collision. 
 *  ======================================================================*/
object ComputeLayoutFiles extends AstStateVisitor {

  type State = LayoutWriterState

  override def defModuleAnnotatedNode(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val data = node1.data
    visitList(s, data.members, matchModuleMember)
  }
  
  override def defTopologyAnnotatedNode(s: State, aNode: Ast.Annotated[AstNode[Ast.DefTopology]]) = {
    val (_, node, _) = aNode
    val data = node.data
    val loc = Locations.get(node.id)
    val name = s.getName(Symbol.Topology(aNode))
    val dirName = LayoutWriterState.getTopologyDirectoryName(name)
    addMapping(s, dirName, loc)
  }

  override def transUnit(s: State, tu: Ast.TransUnit) =
    visitList(s, tu.members, matchTuMember)

  private def addMapping(s: State, name: String, loc: Location) =
    s.locationMap.get(name) match {
      case Some(prevLoc) => Left(CodeGenError.DuplicateLayoutDirectory(name, loc, prevLoc))
      case None =>
        val locationMap = s.locationMap + (name -> loc)
        Right(s.copy(locationMap = locationMap))
    }

}
