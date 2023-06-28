package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for F Prime autocode */
object AutocodeCppWriter extends CppWriter {

  override def defArrayAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ) = {
    val node = aNode._2
    val data = node.data
    val cppDoc = ArrayCppWriter(s, aNode).write
    CppWriter.writeCppDoc(s, cppDoc)
  }

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val node = aNode._2
    val data = node.data
    val cppDoc = ComponentCppWriter(s, aNode).write
    for {
      s <- CppWriter.writeCppDoc(s, cppDoc)
      s <- visitList(s, data.members, matchComponentMember)
    }
    yield s
  }

  override def defEnumAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ) = {
    val node = aNode._2
    val data = node.data
    val cppDoc = EnumCppWriter(s, aNode).write
    CppWriter.writeCppDoc(s, cppDoc)
  }

  override def defModuleAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val node = aNode._2
    val data = node.data
    visitList(s, data.members, matchModuleMember)
  }

  override def defStructAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ) = {
    val cppDoc = StructCppWriter(s, aNode).write
    CppWriter.writeCppDoc(s, cppDoc)
  }

  override def defPortAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) = {
    val cppDoc = PortCppWriter(s, aNode).write
    CppWriter.writeCppDoc(s, cppDoc)
  }

  override def defTopologyAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    val data = node.data
    val cppDoc = TopologyCppWriter(s, aNode).write
    CppWriter.writeCppDoc(s, cppDoc)
  }

}
