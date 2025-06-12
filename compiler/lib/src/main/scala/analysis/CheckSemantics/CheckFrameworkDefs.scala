package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check F Prime framework definitions */
object CheckFrameworkDefs
  extends Analyzer 
  with ModuleAnalyzer
{

  override def defAliasTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]) = {
    val name = a.getQualifiedName (Symbol.AliasType(aNode)).toString
    val id = aNode._2.id
    name match {
      case "FwChanIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwEventIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwOpcodeType" => requireIntegerTypeAlias(a, name, id)
      case "FwPacketDescriptorType" => requireIntegerTypeAlias(a, name, id)
      case "FwTlmPacketizeIdType" => requireIntegerTypeAlias(a, name, id)
      case _ => Right(a)
    }
  }

  private def requireIntegerTypeAlias(a: Analysis, name: String, id: AstNode.Id) =
    if a.typeMap(id).getUnderlyingType.isInt
    then Right(a)
    else Left(
      SemanticError.InvalidType(
        Locations.get(a.useDefMap(id).getNodeId),
        s"the F Prime framework type ${name} must be an alias of an integer type"
      )
    )

}
