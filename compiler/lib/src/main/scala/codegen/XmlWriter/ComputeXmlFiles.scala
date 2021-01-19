package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** ====================================================================== 
 *  Compute the names of the XML files to generate
 *  ====================================================================== 
 *  Check for duplicates that would cause a name collision. 
 *  For example, module M { struct S { ... } } and struct S { ... } will both
 *  generate SSerializableAi.xml, so we can't generate XML files for both
 *  in the same place. 
 *  ======================================================================*/
object ComputeXmlFiles extends AstStateVisitor {

  type State = XmlWriterState

  override def defArrayAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node1, _) = node
    val data = node1.data
    val loc = Locations.get(node1.id)
    val name = s.getSymbolName(Symbol.Array(node))
    val fileName = XmlWriterState.getArrayFileName(name)
    addMapping(s, fileName, loc)
  }

  override def defComponentAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefComponent]]) = {
    val (_, node1, _) = node
    val data = node1.data
    val loc = Locations.get(node1.id)
    val fileName = XmlWriterState.getComponentFileName(data.name)
    for {
      s <- visitList(s, data.members, matchComponentMember)
      s <- addMapping(s, fileName, loc)
    }
    yield s
  }

  override def defEnumAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.data
    val loc = Locations.get(node1.id)
    val name = s.getSymbolName(Symbol.Enum(node))
    val fileName = XmlWriterState.getEnumFileName(name)
    addMapping(s, fileName, loc)
  }

  override def defModuleAnnotatedNode(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val data = node1.data
    visitList(s, data.members, matchModuleMember)
  }

  override def defPortAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefPort]]) = {
    val (_, node1, _) = node
    val data = node1.data
    val loc = Locations.get(node1.id)
    val fileName = XmlWriterState.getPortFileName(data.name)
    addMapping(s, fileName, loc)
  }

  override def defStructAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node1, _) = node
    val data = node1.data
    val loc = Locations.get(node1.id)
    val name = s.getSymbolName(Symbol.Struct(node))
    val fileName = XmlWriterState.getStructFileName(name)
    addMapping(s, fileName, loc)
  }

  override def transUnit(s: State, tu: Ast.TransUnit) =
    visitList(s, tu.members, matchTuMember)

  private def addMapping(s: State, fileName: String, loc: Location) =
    s.locationMap.get(fileName) match {
      case Some(prevLoc) => Left(CodeGenError.DuplicateXmlFile(fileName, loc, prevLoc))
      case None =>
        val locationMap = s.locationMap + (fileName -> loc)
        Right(s.copy(locationMap = locationMap))
    }

}
