package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

object ComputeAutocodeCppFiles extends ComputeCppFiles {

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    val name = s.getName(Symbol.Component(aNode))
    val loc = Locations.get(node.id)
    for {
      s <- addMappings(s, ComputeCppFiles.FileNames.getComponent(name), Some(loc))
      s <- visitList (s, data.members, matchComponentMember)
    }
    yield s
  }

  override def defConstantAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
  ) = addMappings(s, ComputeCppFiles.FileNames.getConstants, None)

  override def defArrayAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ) = {
    val node = aNode._2
    val name = s.getName(Symbol.Array(aNode))
    val loc = Locations.get(node.id)
    addMappings(s, ComputeCppFiles.FileNames.getArray(name), Some(loc))
  }

  override def defEnumAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ) = {
    val node = aNode._2
    val name = s.getName(Symbol.Enum(aNode))
    val loc = Locations.get(node.id)
    addMappings(s, ComputeCppFiles.FileNames.getEnum(name), Some(loc))
  }

  override def defModuleAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val node = aNode._2
    val data = node.data
    visitList(s, data.members, matchModuleMember)
  }

  override def defStructAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ) = {
    val node = aNode._2
    val name = s.getName(Symbol.Struct(aNode))
    val loc = Locations.get(node.id)
    addMappings(s, ComputeCppFiles.FileNames.getStruct(name), Some(loc))
  }

  override def defPortAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) = {
    val node = aNode._2
    val name = s.getName(Symbol.Port(aNode))
    val loc = Locations.get(node.id)
    addMappings(s, ComputeCppFiles.FileNames.getPort(name), Some(loc))
  }

  override def defTopologyAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    val name = node.data.name
    val loc = Locations.get(node.id)
    addMappings(s, ComputeCppFiles.FileNames.getTopology(name), Some(loc))
  }

}
