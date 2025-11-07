package fpp.compiler.transform

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Expand any template expansion specifiers that have not yet been expanded */
object ExpandTemplates extends AstTransformer
{

  type State = Analysis

  type In = State

  type Out = Boolean

  def default(a: Analysis) =
    throw new InternalError("FppExpandTemplates: Transformer not implemented")

  /** Transform a list in sequence, threading state */
  def transformList[A, B](
    s: State,
    list: List[A],
    transform: (State, A) => Result[B]
  ): Result[List[B]] = {
    def helper(res: Boolean, in: List[A], out: List[B]): Result[List[B]] = {
      in match {
        case Nil => Right((res, out))
        case head :: tail => transform(s, head) match {
          case Left(e) => Left(e)
          case Right((newRes, list)) => helper(newRes || res, tail, list :: out)
        }
      }
    }
    for { pair <- helper(false, list, Nil) }
    yield (pair._1, pair._2.reverse)
  }

  override def transUnit(
    a: Analysis,
    tu: Ast.TransUnit
  ): Result[Ast.TransUnit] = {
    for {
      members <- transformList(a, tu.members, matchModuleMember)
    } yield (members._1, Ast.TransUnit(members._2))
  }

  // Duplicate every node inside a template definition
  override def defaultNode[T](
    a: Analysis,
    node: AstNode[T]
  ) =
    a.template match {
      case None => Right(false, node)
      case Some(expansionNode) => {
        val out = AstNode.create(node.data)
        val inLoc = Locations.get(node.id)
        Locations.put(out.id, Location(
          inLoc.file,
          inLoc.pos,
          Some(LocationExpanded(Locations.get(expansionNode)))
        ))
        Right(false, out)
      }
    }

  override def defaultAnnotatedNode[T](
    a: In,
    aNode: Ast.Annotated[AstNode[T]]
  ) = {
    a.template match {
      // We are not currently in a template, leave the node alone
      case None => Right(false, aNode)
      case Some(_) =>
        val (pre, node, post) = aNode
        for (inter <- defaultNode(a, node))
          yield (inter._1, (pre, inter._2, post))
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

    // We are not quite ready to expand the template yet
    // We may _already_ be in the middle of a template expansion in which
    // case we should not be looking in the analysis for symbols since it's probably
    // out of date and we need to enter symbols again.
    // We may have already expanded this template as well in which case we should just walk
    // the child nodes to make sure what we expanded last time is fully expanded now.

    // Check if we are currently expanding a template
    a.template match {
      case None => {
        // Not currently inside a template expansion, we can expand
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
            data.members match {
              // This template expansion has already been expanded
              // We should pass over the inner nodes to make sure those are recursively expanded
              case Some(members) => transformList(
                a.copy(templateStack=tmpl :: a.templateStack),
                members,
                matchModuleMember
              )
              case None => transformList(
                a.copy(
                  template=Some(node.id),
                  templateStack=tmpl :: a.templateStack
                ),
                tmpl.node._2.data.members,
                matchModuleMember
              )
            }
          }
        } yield {
          // Paste the expanded template expansion specifier
          (members._1, (pre, AstNode.create(data.copy(members=Some(members._2)), node.id), post))
        }
      }
      case Some(value) => {
        // We are currently expanding a parent template
        // Tell the compiler to re-run this pass once it's done with this run
        Right((true, aNode))
      }
    }
  }
}
