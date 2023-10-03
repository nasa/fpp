package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component implementation templates */
case class TestCppWriter(testHelperMode: CppWriter.TestHelperMode)
  extends CppWriter
{

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    for {
      s <- CppWriter.writeCppDoc(s, ComponentTesterBaseWriter(s, aNode).write)
      s <- CppWriter.writeCppDoc(s, ComponentGTestBaseWriter(s, aNode).write)
      s <- testHelperMode match {
        // If test helper mode is auto, then the test helpers are part of the autocode
        case CppWriter.TestHelperMode.Auto =>
          val implWriter = ComponentTestImplWriter(s, aNode)
          CppWriter.writeCppFile(s, implWriter.write, Some(implWriter.helperFileName))
        // Otherwise they are part of the implementation
        case CppWriter.TestHelperMode.Manual => Right(s)
      }
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
