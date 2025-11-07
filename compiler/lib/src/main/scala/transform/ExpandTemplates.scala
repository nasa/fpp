package fpp.compiler.transform

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

object ExpandTemplates extends AstStateTransformer
{

  type State = Analysis

  type In = State

  type Out = State

  def default(a: Analysis) =
    throw new InternalError("FppExpandTemplates: Transformer not implemented")

  override def transUnit(
    a: Analysis,
    tu: Ast.TransUnit
  ): Result[Ast.TransUnit] = {
    for {
      members <- transformList(a, tu.members, matchModuleMember)
    } yield (a, Ast.TransUnit(members._2))
  }

  // Duplicate every node inside a template definition
  override def defaultNode[T](
    a: Analysis,
    node: AstNode[T]
  ) =
    a.template match {
      case None => Right(a, node)
      case Some(_) => {
        val out = AstNode.create(node.data)
        Locations.put(out.id, Locations.get(node.id))
        Right(a, out)
      }
    }

  override def defaultAnnotatedNode[T](
    a: In,
    aNode: Ast.Annotated[AstNode[T]]
  ) = {
    a.template match {
      // We are not currently in a template, leave the node alone
      case None => Right(a, aNode)
      case Some(_) =>
        val (pre, node, post) = aNode
        for {
          inter <- defaultNode(a, node)
        } yield (inter._1, (pre, inter._2, post))
    }
  }

  override def defModuleAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefModule(name, members) = node1.data
    for { result <- transformList(a, members, matchModuleMember) }
    yield {
      val (a1, members1) = result
      val defModule = Ast.DefModule(name, members1)
      val node2 = AstNode.create(defModule, node1.id)
      (a1, (pre, node2, post))
    }
  }

  override def specTemplateExpandAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecTemplateExpand]]
  ): ResultAnnotatedNode[Ast.SpecTemplateExpand] = {
    val parentTemplate = a.template
    val (pre, node, post) = aNode
    val data = node.data

    for {
      // Look up the template def
      tmpl <- a.getTemplateSymbol(data.template.id)

      // Make sure attempting to expand this won't cause a cycle
      // i.e. check that we are not in the process of expanding this template
      _ <- {
        a.templateStack.find(t => tmpl == t) match {
          case Some(_) => Left(TemplateExpansionError.Cycle(
            Locations.get(node.id),
            "template expansion cycle"
          ))
          case None => Right(())
        }
      }

      members <- {
        transformList(a.copy(
          template=Some(tmpl),
          templateStack=tmpl :: a.templateStack
        ), tmpl.node._2.data.members, matchModuleMember)
      }

    } yield {
      // Paste the expanded template expansion specifier
      (a, (pre, AstNode.create(data.copy(members=members._2), node.id), post))
    }
  }
}
