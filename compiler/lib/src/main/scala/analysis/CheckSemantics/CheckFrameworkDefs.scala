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
      case "FwAssertArgType" => requireIntegerTypeAlias(a, name, id)
      case "FwChanIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwDpIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwDpPriorityType" => requireIntegerTypeAlias(a, name, id)
      case "FwEnumStoreType" => requireIntegerTypeAlias(a, name, id)
      case "FwEventIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwIndexType" => requireSignedIntegerTypeAlias(a, name, id)
      case "FwOpcodeType" => requireIntegerTypeAlias(a, name, id)
      case "FwPacketDescriptorType" => requireIntegerTypeAlias(a, name, id)
      case "FwPrmIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwQueuePriorityType" => requireIntegerTypeAlias(a, name, id)
      case "FwSignedSizeType" => requireSignedIntegerTypeAlias(a, name, id)
      case "FwSizeStoreType" => requireIntegerTypeAlias(a, name, id)
      case "FwSizeType" => requireUnsignedIntegerTypeAlias(a, name, id)
      case "FwPriorityType" => requireIntegerTypeAlias(a, name, id)
      case "FwTimeBaseStoreType" => requireIntegerTypeAlias(a, name, id)
      case "FwTimeContextStoreType" => requireIntegerTypeAlias(a, name, id)
      case "FwTlmPacketizeIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwTraceIdType" => requireIntegerTypeAlias(a, name, id)
      case _ => Right(a)
    }
  }

  private def requireIntegerTypeAlias(a: Analysis, name: String, id: AstNode.Id) =
    if a.typeMap(id).getUnderlyingType.isInt
    then Right(a)
    else Left(
      SemanticError.InvalidType(
        Locations.get(id),
        s"the F Prime framework type ${name} must be an alias of an integer type"
      )
    )

  private def requireSignedIntegerTypeAlias(a: Analysis, name: String, id: AstNode.Id) = {
    val valid = a.typeMap(id).getUnderlyingType match {
      case pi: Type.PrimitiveInt => pi.signedness == Type.PrimitiveInt.Signed
      case _ => false
    }
    if (valid) then Right(a) else Left(
      SemanticError.InvalidType(
        Locations.get(id),
        s"the F Prime framework type ${name} must be an alias of a signed integer type"
      )
    )
  }

  private def requireUnsignedIntegerTypeAlias(a: Analysis, name: String, id: AstNode.Id) = {
    val valid = a.typeMap(id).getUnderlyingType match {
      case pi: Type.PrimitiveInt => pi.signedness == Type.PrimitiveInt.Unsigned
      case _ => false
    }
    if (valid) then Right(a) else Left(
      SemanticError.InvalidType(
        Locations.get(id),
        s"the F Prime framework type ${name} must be an alias of an unsigned integer type"
      )
    )
  }

}
