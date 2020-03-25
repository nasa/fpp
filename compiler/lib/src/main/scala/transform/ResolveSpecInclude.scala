package fpp.compiler.transform

import fpp.compiler.ast._
import fpp.compiler.syntax._
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

  private def checkForCycle(includingLoc: Location, includedPath: String): Result.Result[Unit] = {
    def checkLoc(locOpt: Option[Location], visitedPaths: List[String]): Result.Result[Unit] = {
      locOpt match {
        case None => Right(())
        case Some(loc) => {
          val path = loc.file.toString
          val visitedPaths1 = path :: visitedPaths
          if (path == includedPath) {
            val msg = "include cycle:\n" ++ visitedPaths1.map("  " ++ _).mkString(" includes\n")
            Left(IncludeError.Cycle(includingLoc, msg))
          }
          else {
            checkLoc(loc.includingLoc, visitedPaths1)
          }
        }
      }
    }
    includingLoc.file match {
      case File.StdIn => Right(())
      case _ => checkLoc(Some(includingLoc), List(includedPath))
    }
  }
  
  private def moduleMember(in: In, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] = {
    def visitSpecInclude(
      in: In, 
      node: Ast.Annotated[AstNode[Ast.SpecInclude]]
    ): Result[List[Ast.ModuleMember]] = {
      val (pre, node1, post) = node
      val spec = node1.getData
      val includingLoc = Locations.get(node1.getId)
      for { 
        path <- includingLoc.relativePath(spec.file) 
        _ <- checkForCycle(includingLoc, path.toString)
        members <- {
          val includedFile = File.Path(path)
          val result = Parser.parseIncludedFile (Parser.moduleMembers) (includedFile, includingLoc)
          result
        }
        pair <- transformList(in, members, moduleMember)
      }
      yield {
        val (data, list) = pair
        (data, list.flatten)
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
