package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check F Prime framework definitions */
object CheckFrameworkDefs
  extends Analyzer
  with ModuleAnalyzer
{

  def getFrameworkDefUses(a: Analysis, s: Symbol) = {
    val usedSymbols = s match
      case Symbol.Array(aNode) =>
        val Right(updatedAnalysis) = UsedSymbols.defArrayAnnotatedNode(a, aNode)
        UsedSymbols.resolveUses(a, updatedAnalysis.usedSymbolSet)
      case Symbol.AliasType(aNode) =>
        val Right(updatedAnalysis) = UsedSymbols.defAliasTypeAnnotatedNode(a, aNode)
        UsedSymbols.resolveUses(a, updatedAnalysis.usedSymbolSet)
      case Symbol.Struct(aNode) =>
        val Right(updatedAnalysis) = UsedSymbols.defStructAnnotatedNode(a, aNode)
        UsedSymbols.resolveUses(a, updatedAnalysis.usedSymbolSet)
      case Symbol.Constant(aNode) =>
        a.typeMap(aNode._2.id) match
          case Type.Enum(enumNode, _, _) => Set(Symbol.Enum(enumNode))
          case _ => Set()
      case _ => Set()
    Right(a.copy(frameworkUsedSymbolSet = a.frameworkUsedSymbolSet ++ usedSymbols))
  }

  override def defAbsTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    val name = a.getQualifiedName (Symbol.AbsType(aNode)).toString
    val id = aNode._2.id
    checkFrameworkTypes(a, name, id)
  }

  override def defAliasTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]) = {
    val s = Symbol.AliasType(aNode)
    val name = a.getQualifiedName (s).toString
    val id = aNode._2.id
    for {
      a <- checkFrameworkTypes(a, name, id)
      a <- name match {
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
      a <- getFrameworkDefUses(a, s)
    } yield a
  }

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val s = Symbol.Array(aNode)
    val name = a.getQualifiedName (s).toString
    val id = aNode._2.id
    for {
      a <- checkFrameworkTypes(a, name, id)
      a <- getFrameworkDefUses(a, s)
    } yield a
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val s = Symbol.Enum(aNode)
    val name = a.getQualifiedName (s).toString
    val id = aNode._2.id
    for {
      a <- checkFrameworkTypes(a, name, id)
      a <- getFrameworkDefUses(a, s)
    } yield a
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val s = Symbol.Struct(aNode)
    val name = a.getQualifiedName (s).toString
    val id = aNode._2.id
    for {
      a <- checkFrameworkTypes(a, name, id)
      a <- getFrameworkDefUses(a, s)
    } yield a
  }

  private def checkFrameworkTypes(a: Analysis, name: String, id: AstNode.Id) = {
    name match {
      case "FwAssertArgType" => requireAliasType(a, name, id)
      case "FwChanIdType" => requireAliasType(a, name, id)
      case "FwDpIdType" => requireAliasType(a, name, id)
      case "FwDpPriorityType" => requireAliasType(a, name, id)
      case "FwEnumStoreType" => requireAliasType(a, name, id)
      case "FwEventIdType" => requireAliasType(a, name, id)
      case "FwIndexType" => requireAliasType(a, name, id)
      case "FwOpcodeType" => requireAliasType(a, name, id)
      case "FwPacketDescriptorType" => requireAliasType(a, name, id)
      case "FwPrmIdType" => requireAliasType(a, name, id)
      case "FwQueuePriorityType" => requireAliasType(a, name, id)
      case "FwSignedSizeType" => requireAliasType(a, name, id)
      case "FwSizeStoreType" => requireAliasType(a, name, id)
      case "FwSizeType" => requireAliasType(a, name, id)
      case "FwPriorityType" => requireAliasType(a, name, id)
      case "FwTimeBaseStoreType" => requireAliasType(a, name, id)
      case "FwTimeContextStoreType" => requireAliasType(a, name, id)
      case "FwTlmPacketizeIdType" => requireAliasType(a, name, id)
      case "FwTraceIdType" => requireAliasType(a, name, id)
      case "Fw.DpState" => requireEnum(a, name, id)
      case "Fw.DpCfg.ProcType" => requireEnum(a, name, id)
      case _ => Right(a)
    }
  }

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val s = Symbol.Constant(aNode)
    val name = a.getQualifiedName (s).toString
    val id = aNode._2.id
    for {
      a <- name match {
        case "Fw.DpCfg.CONTAINER_USER_DATA_SIZE" => requireIntegerConstant(a, name, id)
        case _ => Right(a)
      }
      a <- getFrameworkDefUses(a, s)
    } yield a
  }

  private def requireAliasType(a: Analysis, name: String, id: AstNode.Id) = {
    val t = a.typeMap(id)
    t match {
      case x: Type.AliasType => Right(a)
      case _ =>  Left(
        SemanticError.InvalidType(
          Locations.get(id),
          s"the F Prime framework type ${name} must be an alias type"
        )
      )
    }
  }

  private def requireEnum(a: Analysis, name: String, id: AstNode.Id) = {
    val t = a.typeMap(id)
    t match {
      case x: Type.Enum => Right(a)
      case _ =>  Left(
        SemanticError.InvalidType(
          Locations.get(id),
          s"the F Prime framework type ${name} must be an enum"
        )
      )
    }
  }

  private def requireIntegerConstant(a: Analysis, name: String, id: AstNode.Id) =
    if a.typeMap(id).getUnderlyingType.isInt
    then Right(a)
    else Left(
      SemanticError.InvalidType(
        Locations.get(id),
        s"the F Prime framework constant ${name} must have an integer type"
      )
    )

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
