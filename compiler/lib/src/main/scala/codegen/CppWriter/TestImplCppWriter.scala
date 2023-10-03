package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component unit test templates */
case class TestImplCppWriter(testHelperMode: CppWriter.TestHelperMode)
  extends CppWriter
{

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    val implWriter = ComponentTestImplWriter(s, aNode)
    val cppFileNameBases = testHelperMode match {
      // If test helper mode is auto, then the test helpers are part of the autocode
      case CppWriter.TestHelperMode.Auto => Nil
      // Otherwise they are part of the implementation
      case CppWriter.TestHelperMode.Manual => List(implWriter.helperFileName)
    }
    for {
      s <- CppWriter.writeCppDoc(s, implWriter.write, cppFileNameBases)
      s <- CppWriter.writeCppFile(s, ComponentTestMainWriter(s, aNode).write)
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
