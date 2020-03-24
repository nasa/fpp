package fpp.compiler.transform

import fpp.compiler.ast._
import fpp.compiler.util._

/** Resolve include specifiers */
object ResolveSpecInclude extends AstTransformer {

  def default(in: In) = in

  def transUnit(tuIn: Ast.TransUnit): Result.Result[Ast.TransUnit] = {
    for { result <- transUnit((), tuIn) }
    yield {
      val (_, tuOut) = result
      tuOut
    }
  }

  override def defModuleAnnotatedNode(
    in: In,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (pre, node1, post) = node
    val defModule = node1.getData
    for { result <- transformList(in, defModule.members, moduleMember) }
    yield {
      val (dataOut, members) = result
      val defModule1 = Ast.DefModule(defModule.name, members.flatten)
      val node2 = AstNode.create(defModule1, node1.getId)
      (dataOut, (pre, node2, post))
    }
  }

  override def transUnit(in: In, tu: Ast.TransUnit) = {
    for { result <- transformList(in, tu.members, tuMember) } 
    yield {
      val (dataOut, members) = result
      (dataOut, Ast.TransUnit(members.flatten))
    }
  }
  
  private def moduleMember(in: In, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] = {
    def visitSpecInclude(
      in: In, 
      node: Ast.Annotated[AstNode[Ast.SpecInclude]]
    ): Result[List[Ast.ModuleMember]] = {
      System.out.println(s"visiting ${node}")
      val (pre, node1, post) = node
      val spec = node1.getData
      val loc = Locations.get(node1.getId).tuLocation
      for { 
        path <- loc.relativePath(spec.file) 
        reader <- File.Path(path).open(loc)
      }
      yield { 
        System.out.println(s"  path=${path}")
        val member = Ast.ModuleMember(pre, Ast.ModuleMember.SpecInclude(node1), post)
        (in, List(member))
      }
    }
    val (pre, node, post) = member.node
    node match {
      case Ast.ModuleMember.SpecInclude(node1) => visitSpecInclude(in, (pre, node1, post))
      case _ => for { result <- matchModuleMember(in, member) } 
      yield {
        val (data, member) = result
        (data, List(member))
      }
    }
  }

  private def transformList[A,B](
    in: In,
    members: List[A],
    transform: (In, A) => Result[B]
  ): Result[List[B]] = {
    members match {
      case Nil => Right(in, Nil)
      case head :: tail => for { 
        result1 <- transform(in, head) 
        result2 <- {
          val (dataOut, _) = result1
          transformList(dataOut, tail, transform)
        }
      }
      yield {
        val (_, members1) = result1
        val (dataOut, members2) = result2
        (dataOut, members1 :: members2)
      }
    }
  }

  private def tuMember(in: In, tum: Ast.TUMember) = moduleMember(in, tum)

  type In = Unit

  type Out = Unit

}
