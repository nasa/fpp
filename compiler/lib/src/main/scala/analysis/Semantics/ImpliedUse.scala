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

  def asExprNode: AstNode[Ast.Expr] = {
    val Name.Qualified(qualifier, base) = name
    val head :: tail = name.toIdentList
    val expr = tail.foldLeft (Ast.ExprIdent(head): Ast.Expr) ((e1, s) =>
      Ast.ExprDot(AstNode.create(e1, id), AstNode.create(s, id))
    )
    AstNode.create(expr, id)
  }

  def asUniqueExprNode: AstNode[Ast.Expr] = {
    val Name.Qualified(qualifier, base) = name
    val head :: tail = name.toIdentList
    val expr = tail.foldLeft (Ast.ExprIdent(head): Ast.Expr) ((e1, s) =>
      Ast.ExprDot(
        AstNode.create(e1, ImpliedUse.replicateId(id)),
        AstNode.create(s, ImpliedUse.replicateId(id))
      )
    )
    AstNode.create(expr, ImpliedUse.replicateId(id))
  }

  def asQualIdentNode: AstNode[Ast.QualIdent] = {
    val nodeList = name.toIdentList.map(AstNode.create(_, id))
    val qualIdent = Ast.QualIdent.fromNodeList(nodeList)
    AstNode.create(qualIdent, id)
  }

  def asTypeNameNode: AstNode[Ast.TypeName] = {
    val typeName = Ast.TypeNameQualIdent(asQualIdentNode)
    AstNode.create(typeName, id)
  }

}

object ImpliedUse {

  enum Kind:
    case Constant, Type

  type Uses = Map[Kind, Set[ImpliedUse]]

  /** The qualified names of implied type uses.
   *  Each name is a list of identifiers */
  def getTopologyTypes(a: Analysis) =
    if (a.dictionaryGeneration) then
      List("Fw", "DpCfg", "ProcType") ::
      List("Fw", "DpState") ::
      List(
        "FwChanIdType",
        "FwDpIdType",
        "FwDpPriorityType",
        "FwEventIdType",
        "FwOpcodeType",
        "FwPacketDescriptorType",
        "FwSizeType",
        "FwSizeStoreType",
        "FwTimeBaseStoreType",
        "FwTimeContextStoreType",
        "FwTlmPacketizeIdType"
      ).map(List(_))
    else Nil

  /** The qualified names of implied constant uses.
   *  Each name is a list of identifiers */
  def getTopologyConstants(a: Analysis) =
    if (a.dictionaryGeneration) then List(
      List("Fw", "DpCfg", "CONTAINER_USER_DATA_SIZE")
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
