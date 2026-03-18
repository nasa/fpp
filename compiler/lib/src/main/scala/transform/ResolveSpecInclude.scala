package fpp.compiler.transform

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.util._

/** Resolve include specifiers */
object ResolveSpecInclude extends AstStateTransformer
  with ComponentStateTransformer
  with ModuleStateTransformer
  with StateMachineStateTransformer
  with TopologyStateTransformer
{

  type State = Analysis

  def default(a: Analysis) = a

  def transUnitList(a: Analysis, tul: List[Ast.TransUnit]) =
    transformList(a, tul, transUnit)

  override def transUnit(a: Analysis, tu: Ast.TransUnit) = {
    for { result <- transformList(a, tu.members, tuMember) }
    yield (result._1, Ast.TransUnit(result._2.flatten))
  }

  override def componentMember(a: Analysis, member: Ast.ComponentMember): Result[List[Ast.ComponentMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.ComponentMember.SpecInclude(node1) => resolveSpecInclude(
        a,
        node1,
        Parser.componentMembers,
        componentMember
      )
      case _ => matchComponentMember(a, member)
    }
  }

  override def moduleMember(a: Analysis, member: Ast.ModuleMember): Result[List[Ast.ModuleMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.ModuleMember.SpecInclude(node1) => resolveSpecInclude(
        a,
        node1,
        Parser.moduleMembers,
        moduleMember
      )
      case _ => matchModuleMember(a, member)
    }
  }

  override def stateMachineMember(a: Analysis, member: Ast.StateMachineMember): Result[List[Ast.StateMachineMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.StateMachineMember.SpecInclude(node1) => resolveSpecInclude(
        a,
        node1,
        Parser.stateMachineMembers,
        stateMachineMember
      )
      case _ => matchStateMachineMember(a, member)
    }
  }

  override def stateMember(a: Analysis, member: Ast.StateMember): Result[List[Ast.StateMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.StateMember.SpecInclude(node1) => resolveSpecInclude(
        a,
        node1,
        Parser.stateMembers,
        stateMember
      )
      case _ => matchStateMember(a, member)
    }
  }

  override def tlmPacketMember(
    a: Analysis,
    member: Ast.TlmPacketMember
  ): Result[List[Ast.TlmPacketMember]] = {
    member match {
      case Ast.TlmPacketMember.SpecInclude(include) => resolveSpecInclude(
        a,
        include,
        Parser.tlmPacketMembers,
        tlmPacketMember
      )
      case _ => Right(a, List(member))
    }
  }

  override def tlmPacketSetMember(
    a: Analysis,
    member: Ast.TlmPacketSetMember
  ): Result[List[Ast.TlmPacketSetMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.TlmPacketSetMember.SpecInclude(node1) => resolveSpecInclude(
        a,
        node1,
        Parser.tlmPacketSetMembers,
        tlmPacketSetMember
      )
      case _ => matchTlmPacketSetMember(a, member)
    }
  }

  override def topologyMember(a: Analysis, member: Ast.TopologyMember): Result[List[Ast.TopologyMember]] = {
    val (_, node, _) = member.node
    node match {
      case Ast.TopologyMember.SpecInclude(node1) => resolveSpecInclude(
        a,
        node1,
        Parser.topologyMembers,
        topologyMember
      )
      case _ => matchTopologyMember(a, member)
    }
  }

  override def tuMember(a: Analysis, tum: Ast.TUMember) = moduleMember(a, tum)

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

}
