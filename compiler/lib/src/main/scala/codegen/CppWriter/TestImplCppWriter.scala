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
    val implWriter = ComponentTestImplWriter(s, aNode)
    for {
      s <- CppWriter.writeCppDoc(s, implWriter.write)
      s <- visitList(s, data.members, matchComponentMember)
      s <- CppWriter.writeCppDoc(s, implWriter.write, Some(implWriter.helperFileName))
      s <- visitList(s, data.members, matchComponentMember)
      _ <- CppWriter.writeCppFile(s, ComponentTestMainWriter(s, aNode).write)
      s <- visitList(s, data.members, matchComponentMember)
    }
    yield s
  }

}
