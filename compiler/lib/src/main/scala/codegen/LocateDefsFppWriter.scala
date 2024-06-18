package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._
import scala.language.implicitConversions

/** Writes out the locations of definitions */
object LocateDefsFppWriter extends AstVisitor with LineUtils {

  type In = State

  type Out = List[Line]

  case class State(
    /** The base directory for constructing location specifiers */
    val baseDir: Option[String],
    /** The list of enclosing module names */
    val scopeNameList: List[Name.Unqualified] = Nil
  )

  override def default(s: State) = Nil

  override def defAbsTypeAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    writeSpecLoc(s, Ast.SpecLoc.Type, data.name, node)
  }

  override def defArrayAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefArray]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    writeSpecLoc(s, Ast.SpecLoc.Type, data.name, node)
  }

  override def defComponentAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val s1 = s.copy(scopeNameList = data.name :: s.scopeNameList)
    writeSpecLoc(s, Ast.SpecLoc.Component, data.name, node) ++
      data.members.flatMap(matchComponentMember(s1, _))
  }

  override def defStateMachineAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    writeSpecLoc(s, Ast.SpecLoc.StateMachine, data.name, node)
  }

  override def defComponentInstanceAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefComponentInstance]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    writeSpecLoc(s, Ast.SpecLoc.ComponentInstance, data.name, node)
  }

  override def defConstantAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefConstant]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    writeSpecLoc(s, Ast.SpecLoc.Constant, data.name, node)
  }

  override def defEnumAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefEnum]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    writeSpecLoc(s, Ast.SpecLoc.Type, data.name, node)
  }

  override def defModuleAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node, _) = aNode
    val Ast.DefModule(name, members) = node.data
    val s1 = s.copy(scopeNameList = name :: s.scopeNameList)
    members.flatMap(matchModuleMember(s1, _))
  }

  override def defPortAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefPort]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    writeSpecLoc(s, Ast.SpecLoc.Port, data.name, node)
  }

  override def defStructAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefStruct]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    writeSpecLoc(s, Ast.SpecLoc.Type, data.name, node)
  }

  override def defTopologyAnnotatedNode(
    s: State,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    writeSpecLoc(s, Ast.SpecLoc.Topology, data.name, node)
  }

  override def transUnit(s: State, tu: Ast.TransUnit) =
    tu.members.flatMap(matchModuleMember(s, _))

  private def writeSpecLoc[T](
    s: State,
    kind: Ast.SpecLoc.Kind,
    name: String,
    node: AstNode[T]
  ): Out = {
    val loc = Locations.get(node.id).tuLocation
    loc.file match {
      case File.Path(path) => {
        val nodeList = (name :: s.scopeNameList).reverse.map(s => AstNode.create(s))
        val qualIdentNode = AstNode.create(Ast.QualIdent.fromNodeList(nodeList))
        val baseDir = s.baseDir match {
          case Some(dir) => dir
          case None => ""
        }
        val baseDirPath = java.nio.file.Paths.get(baseDir).toAbsolutePath
        val relativePath = baseDirPath.relativize(path)
        val fileNode = AstNode.create(relativePath.normalize.toString)
        val specLocNode = AstNode.create(Ast.SpecLoc(kind, qualIdentNode, fileNode))
        val specLocAnnotatedNode = (Nil, specLocNode, Nil)
        FppWriter.specLocAnnotatedNode((), specLocAnnotatedNode)
      }
      case File.StdIn => Nil
    }
  }

}
