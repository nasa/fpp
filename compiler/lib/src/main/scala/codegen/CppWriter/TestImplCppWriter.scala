package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component unit test templates */
object TestImplCppWriter extends CppWriter {

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    for {
      s <- CppWriter.writeCppDoc(s, ComponentTestImplWriter(s, aNode).write)
      s <- visitList(s, data.members, matchComponentMember)
    }
    yield s
  }

}
