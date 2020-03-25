package fpp.compiler.transform

import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.util._

/** Resolve include specifiers */
object ResolveSpecInclude extends AstTransformer {

  def default(in: Unit) = ()

  def transUnit(tuIn: Ast.TransUnit): Result.Result[Ast.TransUnit] = {
    for { result <- transUnit((), tuIn) }
    yield result._2
  }

  override def defComponentAnnotatedNode(
    in: Unit,
    node: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefComponent(kind, name, members) = node1.getData
    for { result <- transformList(members, componentMember) }
    yield {
      val (_, members1) = result
      val defComponent = Ast.DefComponent(kind, name, members1.flatten)
      val node2 = AstNode.create(defComponent, node1.getId)
      ((), (pre, node2, post))
    }
  }

  override def defModuleAnnotatedNode(
    in: Unit,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefModule(name, members) = node1.getData
    for { result <- transformList(members, moduleMember) }
    yield {
      val (_, members1) = result
      val defModule = Ast.DefModule(name, members1.flatten)
      val node2 = AstNode.create(defModule, node1.getId)
      ((), (pre, node2, post))
    }
  }

  override def transUnit(in: Unit, tu: Ast.TransUnit) = {
    for { result <- transformList(tu.members, tuMember) } 
    yield ((), Ast.TransUnit(result._2.flatten))
  }

  private def checkForCycle(includingLoc: Location, includedPath: String): Result.Result[Unit] = {
    def checkLoc(locOpt: Option[Location], visitedPaths: List[String]): Result.Result[Unit] =
      locOpt match {
        case None => Right(())
        case Some(loc) => {
          val path = loc.file.toString
          val visitedPaths1 = path :: visitedPaths
          if (path == includedPath) {
            val msg = "include cycle:\n" ++ visitedPaths1.map("  " ++ _).mkString(" includes\n")
            Left(IncludeError.Cycle(includingLoc, msg))
          }
          else checkLoc(loc.includingLoc, visitedPaths1)
        }
      }
    includingLoc.file match {
      case File.StdIn => Right(())
      case _ => checkLoc(Some(includingLoc), List(includedPath))
    }
  }
  
  private def resolveSpecInclude[MemberType](
    node: AstNode[Ast.SpecInclude],
    parser: Parser.Parser[List[MemberType]],
    transformer: (Unit, MemberType) => Result[List[MemberType]]
  ): Result[List[MemberType]] = {
    val spec = node.getData
    val includingLoc = Locations.get(node.getId)
    for { 
      path <- includingLoc.relativePath(spec.file) 
      _ <- checkForCycle(includingLoc, path.toString)
      members <- {
        val includedFile = File.Path(path)
        Parser.parseFile (parser) (Some(includingLoc)) (includedFile)
      }
      pair <- transformList(members, transformer)
    }
    yield ((), pair._2.flatten)
  }

  private def componentMember(in: Unit, member: Ast.ComponentMember): Result[List[Ast.ComponentMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.ComponentMember.SpecInclude(node1) => resolveSpecInclude(
        node1,
        Parser.componentMembers,
        componentMember
      )
      case _ => for { result <- matchComponentMember(in, member) } 
        yield ((), List(result._2))
    }
  }

  private def moduleMember(in: Unit, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.ModuleMember.SpecInclude(node1) => resolveSpecInclude(
        node1,
        Parser.moduleMembers,
        moduleMember
      )
      case _ => for { result <- matchModuleMember(in, member) } 
        yield ((), List(result._2))
    }
  }

  private def topologyMember(in: Unit, member: Ast.TopologyMember): Result[List[Ast.TopologyMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.TopologyMember.SpecInclude(node1) => resolveSpecInclude(
        node1,
        Parser.topologyMembers,
        topologyMember
      )
      case _ => for { result <- matchTopologyMember(in, member) } 
      yield ((), List(result._2))
    }
  }

  private def transformList[A,B](
    members: List[A],
    transform: (Unit, A) => Result[B]
  ): Result[List[B]] = {
    def helper(in: List[A], out: List[B]): Result[List[B]] = {
      in match {
        case Nil => Right((), out)
        case head :: tail => transform((), head) match {
          case Left(e) => Left(e)
          case Right((_, members)) => helper(tail, members :: out)
        }
      }
    }
    helper(members, Nil)
  }

  private def tuMember(in: Unit, tum: Ast.TUMember) = moduleMember(in, tum)

  type In = Unit

  type Out = Unit

}
