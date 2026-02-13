package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct the implied use map */
object ConstructImpliedUseMap
  extends Analyzer
  with ComponentAnalyzer
  with InterfaceAnalyzer
  with ModuleAnalyzer
{

  override def specPortInstanceAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]) = {
    val (_, node, _) = aNode
    val data = node.data
    val id = node.id
    data match {
      case _ : Ast.SpecPortInstance.General => Right(a)
      case special : Ast.SpecPortInstance.Special =>
        // Construct the port use implied by the special port instance
        val name = special.kind match {
          case Ast.SpecPortInstance.CommandRecv => "Cmd"
          case Ast.SpecPortInstance.CommandReg => "CmdReg"
          case Ast.SpecPortInstance.CommandResp => "CmdResponse"
          case Ast.SpecPortInstance.Event => "Log"
          case Ast.SpecPortInstance.ParamGet => "PrmGet"
          case Ast.SpecPortInstance.ParamSet => "PrmSet"
          case Ast.SpecPortInstance.ProductGet => "DpGet"
          case Ast.SpecPortInstance.ProductRecv => "DpResponse"
          case Ast.SpecPortInstance.ProductRequest => "DpRequest"
          case Ast.SpecPortInstance.ProductSend => "DpSend"
          case Ast.SpecPortInstance.Telemetry => "Tlm"
          case Ast.SpecPortInstance.TextEvent => "LogText"
          case Ast.SpecPortInstance.TimeGet => "Time"
        }
        val identList = List("Fw", name)
        val impliedUse = ImpliedUse.fromIdentListAndId(identList, node.id)
        val map = Map(ImpliedUse.Kind.Port -> Set(impliedUse))
        Right(a.copy(impliedUseMap = a.impliedUseMap + (id -> map)))
    }
  }

  override def defStateMachineAnnotatedNodeInternal(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefStateMachine]],
    members: List[Ast.StateMachineMember]
  ) = {
    val id = aNode._2.id
    val sym = Symbol.StateMachine(aNode)
    val qualifier = a.getQualifiedName(sym)
    val name = Name.Qualified(qualifier.toIdentList, "State")
    val impliedUse = ImpliedUse.fromNameAndId(name, id)
    val map = Map(ImpliedUse.Kind.Type -> Set(impliedUse))
    //Right(a.copy(impliedUseMap = a.impliedUseMap + (id -> map)))
    // TODO
    Right(a)
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val id = aNode._2.id
    val typeNames = ImpliedUse.getTopologyTypes(a)
    val empty: ImpliedUse.Uses = Map()
    val typeMap = typeNames.foldLeft (empty) ((m, tn) => {
      val id1 = ImpliedUse.replicateId(id)
      val impliedUse = ImpliedUse.fromIdentListAndId(tn, id1)
      val set = m.get(ImpliedUse.Kind.Type).getOrElse(Set())
      m + (ImpliedUse.Kind.Type -> (set + impliedUse))
    })

    val constants = ImpliedUse.getTopologyConstants(a)
    val map = constants.foldLeft (typeMap) ((m, c) => {
      val id1 = ImpliedUse.replicateId(id)
      val impliedUse = ImpliedUse.fromIdentListAndId(c, id1)
      val set = m.get(ImpliedUse.Kind.Constant).getOrElse(Set())
      m + (ImpliedUse.Kind.Constant -> (set + impliedUse))
    })
    Right(a.copy(impliedUseMap = a.impliedUseMap + (id -> map)))
  }

}
