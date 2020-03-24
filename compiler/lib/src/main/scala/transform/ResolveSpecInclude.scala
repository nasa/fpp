package fpp.compiler.transform

import fpp.compiler.ast._
import fpp.compiler.util._

/** Resolve include specifiers */
object ResolveSpecInclude extends AstTransformer {

  final case class Data(visitedFiles: List[File])

  type In = Data

  type Out = Data

  def default(in: In) = in

  override def transUnit(in: In, tu: Ast.TransUnit) = {
    //val f: (Data, Ast.TUMember) => Result[List[Ast.TUMember]] = tuMember
    def transformList(
      dataIn: Data,
      members: List[Ast.TUMember],
      transform: (Data, Ast.TUMember) => Result[List[Ast.TUMember]]
    ): Result[List[List[Ast.TUMember]]] = {
      members match {
        case Nil => Right(dataIn, Nil)
        case head :: tail => for { 
          result1 <- transform(dataIn, head) 
          result2 <- {
            val (data, _) = result1
            transformList(data, tail, transform)
          }
        }
        yield {
          val (_, members1) = result1
          val (data, members2) = result2
          (data, members1 :: members2)
        }
      }
    }
    for { result <- transformList(in, tu.members, tuMember) } 
    yield {
      val (data, members) = result
      (data, Ast.TransUnit(members.flatten))
    }
  }
  
  private def moduleMember(in: In, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] = {
    def visitSpecInclude(
      in: In, 
      node: Ast.Annotated[AstNode[Ast.SpecInclude]]
    ): Result[List[Ast.ModuleMember]] = {
      val (pre, node1, post) = node
      val member = Ast.ModuleMember(pre, Ast.ModuleMember.SpecInclude(node1), post)
      Right(in, List(member))
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

  private def tuMember(in: In, tum: Ast.TUMember) = moduleMember(in, tum)

}
