package fpp.compiler.transform

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.syntax._
import fpp.compiler.util._

/** Add state enums to state machines */
object AddStateEnums extends AstStateTransformer {

  type State = Unit

  def default(in: State) = in

  override def defModuleAnnotatedNode(
    in: State,
    node: Ast.Annotated[AstNode[Ast.DefModule]]
  ) = {
    val (pre, node1, post) = node
    val Ast.DefModule(name, members) = node1.data
    for (result <- transformList(in, members, moduleMember))
    yield {
      val (_, members1) = result
      val defModule = Ast.DefModule(name, members1.flatten)
      val node2 = AstNode.create(defModule, node1.id)
      (in, (pre, node2, post))
    }
  }

  override def defStateMachineAnnotatedNode(
    in: State,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]]
  ) = {
    val (pre, node, post) = aNode
    val Ast.DefStateMachine(name, members) = node.data
    val id = node.id
    members match {
      case None => Right((in, aNode))
      case Some(members) =>
        for (stateEnum <- getStateEnum(Symbol.StateMachine(aNode)))
        yield {
          val enumNode = AstNode.create(stateEnum, id)
          val enumMemberNode = Ast.StateMachineMember.DefEnum(enumNode)
          val member = Ast.StateMachineMember(Nil, enumMemberNode, Nil)
          val defStateMachine = Ast.DefStateMachine(name, Some(member :: members))
          val node1 = AstNode.create(defStateMachine, node.id)
          (in, (pre, node1, post))
        }
    }
  }

  override def transUnit(in: State, tu: Ast.TransUnit) =
    for (result <- transformList(in, tu.members, tuMember))
      yield (in, Ast.TransUnit(result._2.flatten))

  private def getStateEnumType(n: Int): Ast.TypeName = {
    val typeInt =
      if n < 256 then Ast.U8
      else if n < 65536 then Ast.U16
      else Ast.U32
    Ast.TypeNameInt(typeInt)
  }

  private def getStateEnum(sym: Symbol.StateMachine): Result.Result[Ast.DefEnum] =
    for (enumConstants <- GetEnumConstants.get(sym))
      yield {
        val typeName = getStateEnumType(enumConstants.size)
        val typeNameNode = AstNode.create(typeName)
        Locations.put(typeNameNode.id, Locations.get(sym.getNodeId))
        Ast.DefEnum("State", Some(typeNameNode), enumConstants, None, false)
      }

  private def asList[T](result: Result[T]) =
    result.map(r => (r._1, List(r._2)))

  private def moduleMember(in: State, member: Ast.ModuleMember):
    Result[List[Ast.ModuleMember]] =
    asList(matchModuleMember(in, member))

  private def tuMember(in: State, tum: Ast.TUMember) = moduleMember(in, tum)

  private object GetEnumConstants extends AstStateVisitor {

    case class State(
      // The current qualifier list in the traversal
      qualifiers: List[Ast.Ident] = Nil,
      // The set of annotated state names
      aNames: Set[Ast.Annotated[Ast.Ident]] = Set()
    )

    override def default(s: State) = Right(s)

    override def defStateMachineAnnotatedNodeInternal(
      s: State,
      aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
      members: List[Ast.StateMachineMember]
    ) = visitList(s, members, matchStateMachineMember)

    override def defStateAnnotatedNode(
      s: State,
      aNode: Ast.Annotated[AstNode[Ast.DefState]]
    ) = {
      val (a1, node, a2) = aNode
      val data = node.data
      val nameList = data.name :: s.qualifiers
      fpp.compiler.analysis.State.getSubstates(data) match {
        case Nil =>
          if s.aNames.size == Int.MaxValue - 1
          // This should never happen
          // There should never be over 2 billion leaf states
          then Left(
            SemanticError.StateMachine.TooManyLeafStates(
              Locations.get(node.id)
            )
          )
          else
            val aName = (a1, nameList.reverse.mkString("_"), a2)
            Right(s.copy(aNames = s.aNames + aName))
        case _ =>
          val s1 = s.copy(qualifiers = nameList)
          for (s2 <- visitList(s1, data.members, matchStateMember))
            yield s2.copy(qualifiers = s.qualifiers)
      }
    }

    def get(sym: Symbol.StateMachine):
      Result.Result[List[Ast.Annotated[AstNode[Ast.DefEnumConstant]]]] =
        for {
          s <- GetEnumConstants.defStateMachineAnnotatedNode(
            GetEnumConstants.State(),
            sym.node
          )
        } yield {
          val uninitState = (
            List("The uninitialized state"),
            "__FPRIME_UNINITIALIZED",
            Nil
          )
          val leafStates = s.aNames.toList.sortWith(_._2 < _._2)
          val constants = uninitState :: leafStates
          val constantNodes = constants.map {
            case (a1, name, a2) => (
              a1,
              AstNode.create(Ast.DefEnumConstant(name, None)),
              a2
            )
          }
          constantNodes.map {
            case (_, node, _) => Locations.put(node.id, Locations.get(sym.getNodeId))
          }
          constantNodes
        }

  }
}
