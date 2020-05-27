package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Write out F Prime XML */
object XmlWriter extends AstStateVisitor {

  /** XML Writer state */
  case class State(
    /** The result of semantic analysis */
    a: Analysis,
    /** The output directory */
    dir: String
  )

  override def defArrayAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    // TODO
    default(s)
  }

  override def defEnumAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    // TODO
    default(s)
  }

  override def defModuleAnnotatedNode(
    s: State,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (_, node1, _) = node
    val data = node1.getData
    visitList(s, data.members, matchModuleMember)
  }

  override def defStructAnnotatedNode(s: State, node: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val (_, node1, _) = node
    val data = node1.getData
    // TODO
    default(s)
  }

  override def transUnit(s: State, tu: Ast.TransUnit) = 
    visitList(s, tu.members, matchTuMember)

}
