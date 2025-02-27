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

  override def defPortAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) = {
    val cppDoc = PortCppWriter(s, aNode).write
    CppWriter.writeCppDoc(s, cppDoc)
  }

  override def defStateMachineAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) = StateMachine.getSymbolKind(Symbol.StateMachine(aNode)) match {
    case StateMachine.Kind.Internal =>
      val cppDoc = StateMachineCppWriter(s, aNode).write
      CppWriter.writeCppDoc(s, cppDoc)
    case StateMachine.Kind.External => Right(s)
  }

  override def defStructAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ) = {
    val cppDoc = StructCppWriter(s, aNode).write
    CppWriter.writeCppDoc(s, cppDoc)
  }

  override def defTopologyAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val node = aNode._2
    val data = node.data
    val ts = Symbol.Topology(aNode)
    val t = s.a.topologyMap(ts)
    val d = s.a.dictionaryMap(ts)
    val a = s.a.copy(topology = Some(t), dictionary = Some(d))
    val s1 = s.copy(a = a)
    val cppDoc = TopologyCppWriter(s1, aNode).write
    for {
      s <- CppWriter.writeCppDoc(s1, cppDoc)
      s <- visitList(s, data.members, matchTopologyMember)
    }
    yield s
  }

  override def specTlmPacketSetAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.SpecTlmPacketSet]]
  ) = {
    val cppDoc = TlmPacketSetCppWriter(s, aNode).write
    CppWriter.writeCppDoc(s, cppDoc)
  }

}
