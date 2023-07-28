package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

object ComputeTestImplCppFiles extends ComputeCppFiles {

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    val name = s.getName(Symbol.Component(aNode))
    val loc = Locations.get(node.id)
    for {
      s <- addMappings(s, ComputeCppFiles.FileNames.getComponentTestImpl(name), Some(loc))
      s <- visitList (s, data.members, matchComponentMember)
      s <- addMappings(s, ComputeCppFiles.FileNames.getComponentTestHelper(name), Some(loc))
      s <- visitList(s, data.members, matchComponentMember)
      s <- addMappings(s, ComputeCppFiles.FileNames.getComponentTestMain(name), Some(loc))
      s <- visitList(s, data.members, matchComponentMember)
    }
    yield s
  }

}
