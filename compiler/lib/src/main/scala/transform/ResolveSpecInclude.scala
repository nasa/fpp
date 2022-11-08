package fpp.compiler.transform

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.util._

/** Resolve include specifiers */
object ResolveSpecInclude extends AstStateTransformer {

  type State = Analysis

  def default(a: Analysis) = a

  override def defComponentAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefComponent]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefComponent(kind, name, members) = node1.data
    for { result <- transformList(a, members, componentMember) }
    yield {
      val (a1, members1) = result
      val defComponent = Ast.DefComponent(kind, name, members1.flatten)
      val node2 = AstNode.create(defComponent, node1.id)
      (a1, (pre, node2, post))
    }
  }

  override def defModuleAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefModule(name, members) = node1.data
    for { result <- transformList(a, members, moduleMember) }
    yield {
      val (a1, members1) = result
      val defModule = Ast.DefModule(name, members1.flatten)
      val node2 = AstNode.create(defModule, node1.id)
      (a1, (pre, node2, post))
    }
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    node: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefTopology(name, members) = node1.data
    for { result <- transformList(a, members, topologyMember) }
    yield {
      val (a1, members1) = result
      val defTopology = Ast.DefTopology(name, members1.flatten)
      val node2 = AstNode.create(defTopology, node1.id)
      (a1, (pre, node2, post))
    }
  }

  override def transUnit(a: Analysis, tu: Ast.TransUnit) = {
    for { result <- transformList(a, tu.members, tuMember) } 
    yield (result._1, Ast.TransUnit(result._2.flatten))
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
    a: Analysis,
    node: AstNode[Ast.SpecInclude],
    parser: Parser.Parser[List[MemberType]],
    transformer: (Analysis, MemberType) => Result[List[MemberType]]
  ): Result[List[MemberType]] = {
    val spec = node.data
    val includingLoc = Locations.get(spec.file.id)
    val path = includingLoc.getRelativePath(spec.file.data) 
    for { 
      includedFile <- Right(File.Path(path))
      _ <- checkForCycle(includingLoc, path.toString)
      members <- Parser.parseFile (parser) (Some(includingLoc)) (includedFile)
      pair <- {
        val a1 = a.copy(includedFileSet = a.includedFileSet + includedFile)
        transformList(a1, members, transformer)
      }
    }
    yield (pair._1, pair._2.flatten)
  }

  private def componentMember(a: Analysis, member: Ast.ComponentMember): Result[List[Ast.ComponentMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.ComponentMember.SpecInclude(node1) => resolveSpecInclude(
        a,
        node1,
        Parser.componentMembers,
        componentMember
      )
      case _ => for { result <- matchComponentMember(a, member) } 
        yield (result._1, List(result._2))
    }
  }

  private def moduleMember(a: Analysis, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.ModuleMember.SpecInclude(node1) => resolveSpecInclude(
        a,
        node1,
        Parser.moduleMembers,
        moduleMember
      )
      case _ => for { result <- matchModuleMember(a, member) } 
        yield (result._1, List(result._2))
    }
  }

  private def topologyMember(a: Analysis, member: Ast.TopologyMember): Result[List[Ast.TopologyMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.TopologyMember.SpecInclude(node1) => resolveSpecInclude(
        a,
        node1,
        Parser.topologyMembers,
        topologyMember
      )
      case _ => for { result <- matchTopologyMember(a, member) } 
      yield (result._1, List(result._2))
    }
  }

  private def tuMember(a: Analysis, tum: Ast.TUMember) = moduleMember(a, tum)

}
