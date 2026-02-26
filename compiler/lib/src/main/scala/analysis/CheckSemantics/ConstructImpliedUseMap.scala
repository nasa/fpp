package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Construct the implied use map */
object ConstructImpliedUseMap extends TypeExpressionAnalyzer {

  override def specPortInstanceAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.SpecPortInstance]]
  ) = {
    val (_, node, _) = aNode
    val data = node.data
    val id = node.id
    val a1 = data match {
      case _ : Ast.SpecPortInstance.General => a
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
        a.copy(impliedUseMap = a.impliedUseMap + (id -> map))
    }
    super.specPortInstanceAnnotatedNode(a1, aNode)
  }

  override def defTopologyAnnotatedNode(
    a: Analysis,
    aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
  ) = {
    val id = aNode._2.id
    val typeNames = ImpliedUse.getTopologyTypes(a)
    val empty: ImpliedUse.Uses = Map()
    val annotations = List("this implied use occurs when constructing a dictionary")
    val typeMap = typeNames.foldLeft (empty) ((m, tn) => {
      val id1 = ImpliedUse.replicateId(id)
      val impliedUse = ImpliedUse.fromIdentListAndId(tn, id1, annotations)
      val set = m.get(ImpliedUse.Kind.Type).getOrElse(Set())
      m + (ImpliedUse.Kind.Type -> (set + impliedUse))
    })
    val constants = ImpliedUse.getTopologyConstants(a)
    val map = constants.foldLeft (typeMap) ((m, c) => {
      val id1 = ImpliedUse.replicateId(id)
      val impliedUse = ImpliedUse.fromIdentListAndId(c, id1, annotations)
      val set = m.get(ImpliedUse.Kind.Constant).getOrElse(Set())
      m + (ImpliedUse.Kind.Constant -> (set + impliedUse))
    })
    val a1 = a.copy(impliedUseMap = a.impliedUseMap + (id -> map))
    super.defTopologyAnnotatedNode(a1, aNode)
  }

  override def typeNameStringNode(
    a: Analysis, 
    node: AstNode[Ast.TypeName],
    tn: Ast.TypeNameString
  ) = {
    val id = node.id
    def getImpliedUses(name: String, annotation: String) = {
      val il = List(name)
      val id1 = ImpliedUse.replicateId(id)
      Set(ImpliedUse.fromIdentListAndId(il, id1, List(annotation)))
    }
    val map = {
      val uses = getImpliedUses(
        "FwSizeStoreType",
        "use of a string type requires this definition"
      )
      Map(ImpliedUse.Kind.Type -> uses)
    }
    val map1 = if tn.size.isDefined
      then map
      else {
        val uses = getImpliedUses(
          "FW_FIXED_LENGTH_STRING_SIZE",
          "use of a string type with default size requires this definition"
        )
        map + (ImpliedUse.Kind.Constant -> uses)
      }
    val a1 = a.copy(impliedUseMap = a.impliedUseMap + (id -> map1))
    super.typeNameStringNode(a1, node, tn)
  }

}
