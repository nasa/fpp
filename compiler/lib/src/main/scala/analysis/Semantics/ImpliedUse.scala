package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** An implied use of an FPP symbol */
case class ImpliedUse(
  /** The fully-qualified name of the implied use */
  name: Name.Qualified,
  /** The AST node id associated with the implied use */
  id: AstNode.Id
) {

  def asAstNode = {
    val nodeList = name.toIdentList.map(AstNode.create(_, id))
    val qualIdent = Ast.QualIdent.fromNodeList(nodeList)
    AstNode.create(qualIdent, id)
  }

}

object ImpliedUse {

  enum Kind:
    case Constant, Enum, Type

  type Uses = Map[Kind, Set[ImpliedUse]]

  def getTopologyIntegerTypes(a: Analysis) =
    if (a.dictionaryNeeded) then List(
      "FwChanIdType",
      "FwEventIdType",
      "FwOpcodeType",
      "FwPacketDescriptorType",
      "FwTlmPacketizeIdType"
    )
    else Nil

  def replicateId(id: AstNode.Id) = {
    val loc = Locations.get(id)
    val id1 = AstNode.getId
    Locations.put(id1, loc)
    id1
  }

  def fromIdentListAndId(identList: List[Name.Unqualified], id: AstNode.Id) =
    ImpliedUse(Name.Qualified.fromIdentList(identList), id)

}
