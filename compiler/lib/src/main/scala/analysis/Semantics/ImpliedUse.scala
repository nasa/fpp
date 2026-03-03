package fpp.compiler.analysis

import fpp.compiler.ast.*
import fpp.compiler.util.*

/** An implied use of an FPP symbol */
case class ImpliedUse(
  /** The fully-qualified name of the implied use */
  name: Name.Qualified,
  /** The AST node id associated with the implied use */
  id: AstNode.Id,
  /** Optional annotations for error reporting */
  annotations: List[String] = Nil
) {

  def asExprNode: AstNode[Ast.Expr] = {
    val Name.Qualified(qualifier, base) = name
    val head :: tail = name.toIdentList
    val expr = tail.foldLeft (Ast.ExprIdent(head): Ast.Expr) ((e1, s) =>
      Ast.ExprDot(AstNode.create(e1, id), AstNode.create(s, id))
    )
    AstNode.create(expr, id)
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

  def annotateResult[T](r: Result.Result[T]) = {
    val as = s"the symbol $name has an implied use at the point of the error" :: annotations
    Result.annotateResult(r, as)
  }

}

object ImpliedUse {

  enum Kind:
    case Constant, Port, Type

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
      List("Fw", "DpCfg", "CONTAINER_USER_DATA_SIZE"),
      List("FW_FIXED_LENGTH_STRING_SIZE")
    )
    else Nil

  /** Create a new ID at the same location as id */
  def replicateId(id: AstNode.Id) = {
    val loc = Locations.get(id)
    val id1 = AstNode.getId
    Locations.put(id1, loc)
    id1
  }

  def fromIdentListAndId(
    identList: List[Name.Unqualified],
    id: AstNode.Id,
    annotations: List[String] = Nil
  ) = ImpliedUse(Name.Qualified.fromIdentList(identList), id, annotations)

}
