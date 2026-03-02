package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check F Prime framework definitions */
object CheckFrameworkDefs
  extends Analyzer
  with ComponentAnalyzer
  with ModuleAnalyzer
{

  override def defAbsTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) =
    analyzeType(a, Symbol.AbsType(aNode))

  override def defAliasTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]) =
    analyzeType(a, Symbol.AliasType(aNode))

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) =
    analyzeType(a, Symbol.Array(aNode))

  override def defConstantAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefConstant]]) = {
    val s = Symbol.Constant(aNode)
    val name = a.getQualifiedName(s).toString
    val id = aNode._2.id
    constants.get(name) match {
      case Some(checker) =>
        for a <- checker(a, name, id)
        yield a.copy(frameworkDefinitions = a.frameworkDefinitions.addConstant(name, s))
      case None => Right(a)
    }
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) =
    analyzeType(a, Symbol.Enum(aNode))

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) =
    analyzeType(a, Symbol.Struct(aNode))

  private val constants = Map(
    "FW_ASSERT_COUNT_MAX" -> requireIntegerConstant,
    "FW_CMD_ARG_BUFFER_MAX_SIZE" -> requireIntegerConstant,
    "FW_CMD_STRING_MAX_SIZE" -> requireIntegerConstant,
    "FW_COM_BUFFER_MAX_SIZE" -> requireIntegerConstant,
    "FW_CONTEXT_DONT_CARE" -> requireIntegerConstant,
    "FW_FILE_BUFFER_MAX_SIZE" -> requireIntegerConstant,
    "FW_FIXED_LENGTH_STRING_SIZE" -> requireIntegerConstant,
    "FW_INTERNAL_INTERFACE_STRING_MAX_SIZE" -> requireIntegerConstant,
    "FW_LOG_BUFFER_MAX_SIZE" -> requireIntegerConstant,
    "FW_LOG_STRING_MAX_SIZE" -> requireIntegerConstant,
    "FW_LOG_TEXT_BUFFER_SIZE" -> requireIntegerConstant,
    "FW_OBJ_SIMPLE_REG_BUFF_SIZE" -> requireIntegerConstant,
    "FW_OBJ_SIMPLE_REG_ENTRIES" -> requireIntegerConstant,
    "FW_PARAM_BUFFER_MAX_SIZE" -> requireIntegerConstant,
    "FW_PARAM_STRING_MAX_SIZE" -> requireIntegerConstant,
    "FW_QUEUE_NAME_BUFFER_SIZE" -> requireIntegerConstant,
    "FW_QUEUE_SIMPLE_QUEUE_ENTRIES" -> requireIntegerConstant,
    "FW_SERIALIZE_FALSE_VALUE" -> requireIntegerConstant,
    "FW_SERIALIZE_TRUE_VALUE" -> requireIntegerConstant,
    "FW_SM_SIGNAL_BUFFER_MAX_SIZE" -> requireIntegerConstant,
    "FW_STATEMENT_ARG_BUFFER_MAX_SIZE" -> requireIntegerConstant,
    "FW_TASK_NAME_BUFFER_SIZE" -> requireIntegerConstant,
    "FW_TLM_BUFFER_MAX_SIZE" -> requireIntegerConstant,
    "FW_TLM_STRING_MAX_SIZE" -> requireIntegerConstant,
    "Fw.DpCfg.CONTAINER_USER_DATA_SIZE" -> requireIntegerConstant
  )

  private val types = Map(
    "Fw.DpCfg.ProcType" -> requireEnum,
    "Fw.DpState" -> requireEnum,
    "FwAssertArgType" -> requireIntegerTypeAlias,
    "FwChanIdType" -> requireIntegerTypeAlias,
    "FwDpIdType" -> requireIntegerTypeAlias,
    "FwDpPriorityType" -> requireIntegerTypeAlias,
    "FwEnumStoreType" -> requireIntegerTypeAlias,
    "FwEventIdType" -> requireIntegerTypeAlias,
    "FwIndexType" -> requireSignedIntegerTypeAlias,
    "FwOpcodeType" -> requireIntegerTypeAlias,
    "FwPacketDescriptorType" -> requireIntegerTypeAlias,
    "FwPriorityType" -> requireIntegerTypeAlias,
    "FwPrmIdType" -> requireIntegerTypeAlias,
    "FwQueuePriorityType" -> requireIntegerTypeAlias,
    "FwSignedSizeType" -> requireSignedIntegerTypeAlias,
    "FwSizeStoreType" -> requireIntegerTypeAlias,
    "FwSizeType" -> requireUnsignedIntegerTypeAlias,
    "FwTimeBaseStoreType" -> requireIntegerTypeAlias,
    "FwTimeContextStoreType" -> requireIntegerTypeAlias,
    "FwTlmPacketizeIdType" -> requireIntegerTypeAlias,
    "FwTraceIdType" -> requireIntegerTypeAlias
  )

  private def analyzeType(a: Analysis, s: TypeSymbol) = {
    val name = a.getQualifiedName(s).toString
    val id = s.getNodeId
    types.get(name) match {
      case Some(checker) =>
        for a <- checker(a, name, id)
        yield a.copy(frameworkDefinitions = a.frameworkDefinitions.addType(name, s))
      case None => Right(a)
    }
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
