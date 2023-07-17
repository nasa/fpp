package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component implementation templates */
object ImplCppWriter extends CppWriter {

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    val cppDoc = ComponentImplWriter(s, aNode).write
    for {
      s <- CppWriter.writeCppDoc(s, cppDoc)
      s <- visitList(s, data.members, matchComponentMember)
    }
    yield s
  }

}
