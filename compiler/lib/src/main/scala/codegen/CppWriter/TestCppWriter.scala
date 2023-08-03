package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component implementation templates */
object TestCppWriter extends CppWriter {

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    for {
      s <- CppWriter.writeCppDoc(s, ComponentTesterBaseWriter(s, aNode).write)
      s <- visitList(s, data.members, matchComponentMember)
      s <- CppWriter.writeCppDoc(s, ComponentGTestBaseWriter(s, aNode).write)
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
