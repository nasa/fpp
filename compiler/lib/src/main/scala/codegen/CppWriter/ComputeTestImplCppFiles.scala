package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

case class ComputeTestImplCppFiles(testHelperMode: CppWriter.TestHelperMode)
  extends ComputeCppFiles
{

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
      s <- testHelperMode match {
        // If test helper mode is auto, then the test helpers are part of the autocode
        case CppWriter.TestHelperMode.Auto => Right(s)
        // Otherwise they are part of the implementation
        case CppWriter.TestHelperMode.Manual => 
          addCppMapping(s, ComputeCppFiles.FileNames.getComponentTestHelper(name), Some(loc))
      }
      s <- visitList(s, data.members, matchComponentMember)
      s <- addCppMapping(s, ComputeCppFiles.FileNames.getComponentTestMain(name), Some(loc))
      s <- visitList(s, data.members, matchComponentMember)
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
