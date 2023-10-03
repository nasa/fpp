package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._

case class ComputeTestCppFiles(testHelperMode: CppWriter.TestHelperMode)
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
      s <- addMappings(s, ComputeCppFiles.FileNames.getComponentGTestBase(name), Some(loc))
      s <- visitList (s, data.members, matchComponentMember)
      s <- addMappings(s, ComputeCppFiles.FileNames.getComponentTesterBase(name), Some(loc))
      s <- testHelperMode match {
        // If test helper mode is auto, then the test helpers are part of the autocode
        case CppWriter.TestHelperMode.Auto =>
          addCppMapping(s, ComputeCppFiles.FileNames.getComponentTestHelper(name), Some(loc))
        // Otherwise they are part of the implementation
        case CppWriter.TestHelperMode.Manual => Right(s)
      }
      s <- visitList (s, data.members, matchComponentMember)
    }
    yield s
  }

  override def defModuleAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = visitList(s, aNode._2.data.members, matchModuleMember)

}
