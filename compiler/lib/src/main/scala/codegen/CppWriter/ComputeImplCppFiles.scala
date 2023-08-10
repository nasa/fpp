package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

object ComputeImplCppFiles extends ComputeCppFiles {

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    val name = s.getName(Symbol.Component(aNode))
    val loc = Locations.get(node.id)
    for {
      s <- addMappings(
        s,
        ComputeCppFiles.FileNames.getComponentImpl(name),
        Some(loc),
        "template.hpp",
        "template.cpp"
      )
      s <- visitList (s, data.members, matchComponentMember)
    }
    yield s
  }

  override def defModuleAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val node = aNode._2
    val data = node.data
    visitList(s, data.members, matchModuleMember)
  }

}
