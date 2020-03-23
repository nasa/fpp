package fpp.compiler.transform

import fpp.compiler.ast._
import fpp.compiler.util._

/** Resolve include specifiers */
object AstWriter extends AstTransformer {

  final case class Data(visitedFiles: List[File])

  type In = Data

  type Out = Data

  def default(in: In) = in

  //def transUnit(in: In, tu: Ast.TransUnit) =
  
  private def resolveSpecInclude(
    in: In, 
    annotatedNode: Ast.Annotated[Ast.ModuleMember.SpecInclude]
  ): Result[List[Ast.ModuleMember]] = {
    Right(in, List(Ast.ModuleMember(annotatedNode)))
  }

  private def moduleMember(in: In, mm: Ast.ModuleMember): Result[List[Ast.ModuleMember]] = {
    val (a1, node, a2) = mm.node
    matchModuleMemberNode(in, node) match {
      case Right((out, node1)) => Right(out, List(Ast.ModuleMember(a1, node1, a2)))
      case Left(e) => Left(e)
    }
  }

  private def tuMember(in: In, tum: Ast.TUMember) = moduleMember(in, tum)

}
