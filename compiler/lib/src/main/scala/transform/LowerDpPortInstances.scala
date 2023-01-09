package fpp.compiler.transform

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.util._

/** Lower data product port instances to a form that the Python
 *  autocoder can handle */
object LowerDpPortInstances extends AstStateTransformer {

  type State = Unit

  def default(u: Unit) = u

  /**
  override def defComponentAnnotatedNode(
    u: Unit,
    node: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefComponent(kind, name, members) = node1.data
    for { result <- transformList(u, members, componentMember) }
    yield {
      val (a1, members1) = result
      val defComponent = Ast.DefComponent(kind, name, members1.flatten)
      val node2 = AstNode.create(defComponent, node1.id)
      (a1, (pre, node2, post))
    }
  }

  override def defModuleAnnotatedNode(
    u: Unit,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefModule(name, members) = node1.data
    for { result <- transformList(u, members, moduleMember) }
    yield {
      val (a1, members1) = result
      val defModule = Ast.DefModule(name, members1.flatten)
      val node2 = AstNode.create(defModule, node1.id)
      (a1, (pre, node2, post))
    }
  }
  **/

  override def transUnit(u: Unit, tu: Ast.TransUnit) = {
    for { result <- transformList(u, tu.members, tuMember) } 
    yield (result._1, Ast.TransUnit(result._2.flatten))
  }

  /**
  private def componentMember(u: Unit, member: Ast.ComponentMember):
    Result[List[Ast.ComponentMember]] =
  {
    val (_, node, _) = member.node
    node match {
      case Ast.ComponentMember.SpecInclude(node1) => resolveSpecInclude(
        u,
        node1,
        Parser.componentMembers,
        componentMember
      )
      case _ => for { result <- matchComponentMember(u, member) } 
        yield (result._1, List(result._2))
    }
  }
  **/

  private def moduleMember(u: Unit, member: Ast.ModuleMember):
    Result[List[Ast.ModuleMember]] =
  {
    for { result <- matchModuleMember(u, member) } 
      yield (result._1, List(result._2))
  }

  private def tuMember(u: Unit, tum: Ast.TUMember) = moduleMember(u, tum)

}
