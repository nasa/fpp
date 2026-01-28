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
    for a <- checkConstant(a, name, id)
      yield updateConstants(a, name, s)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) =
    analyzeType(a, Symbol.Enum(aNode))

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) =
    analyzeType(a, Symbol.Struct(aNode))

  private def analyzeType(a: Analysis, s: TypeSymbol) = {
    val name = a.getQualifiedName(s).toString
    val id = s.getNodeId
    for a <- checkType(a, name, id)
      yield updateTypes(a, name, s)
  }

  private def checkConstant(a: Analysis, name: String, id: AstNode.Id) =
    name match {
      case "FW_ASSERT_COUNT_MAX" => requireIntegerConstant(a, name, id)
      case "FW_CMD_ARG_BUFFER_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_CMD_STRING_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_COM_BUFFER_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_CONTEXT_DONT_CARE" => requireIntegerConstant(a, name, id)
      case "FW_FILE_BUFFER_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_FIXED_LENGTH_STRING_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_INTERNAL_INTERFACE_STRING_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_LOG_BUFFER_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_LOG_STRING_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_LOG_TEXT_BUFFER_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_OBJ_SIMPLE_REG_BUFF_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_OBJ_SIMPLE_REG_ENTRIES" => requireIntegerConstant(a, name, id)
      case "FW_PARAM_BUFFER_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_PARAM_STRING_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_QUEUE_NAME_BUFFER_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_QUEUE_SIMPLE_QUEUE_ENTRIES" => requireIntegerConstant(a, name, id)
      case "FW_SERIALIZE_FALSE_VALUE" => requireIntegerConstant(a, name, id)
      case "FW_SERIALIZE_TRUE_VALUE" => requireIntegerConstant(a, name, id)
      case "FW_SM_SIGNAL_BUFFER_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_STATEMENT_ARG_BUFFER_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_TASK_NAME_BUFFER_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_TLM_BUFFER_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "FW_TLM_STRING_MAX_SIZE" => requireIntegerConstant(a, name, id)
      case "Fw.DpCfg.CONTAINER_USER_DATA_SIZE" => requireIntegerConstant(a, name, id)
      case _ => Right(a)
    }

  private def checkType(a: Analysis, name: String, id: AstNode.Id) = {
    name match {
      case "Fw.DpCfg.ProcType" => requireEnum(a, name, id)
      case "Fw.DpState" => requireEnum(a, name, id)
      case "FwAssertArgType" => requireIntegerTypeAlias(a, name, id)
      case "FwChanIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwDpIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwDpPriorityType" => requireIntegerTypeAlias(a, name, id)
      case "FwEnumStoreType" => requireIntegerTypeAlias(a, name, id)
      case "FwEventIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwIndexType" => requireSignedIntegerTypeAlias(a, name, id)
      case "FwOpcodeType" => requireIntegerTypeAlias(a, name, id)
      case "FwPacketDescriptorType" => requireIntegerTypeAlias(a, name, id)
      case "FwPriorityType" => requireIntegerTypeAlias(a, name, id)
      case "FwPrmIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwQueuePriorityType" => requireIntegerTypeAlias(a, name, id)
      case "FwSignedSizeType" => requireSignedIntegerTypeAlias(a, name, id)
      case "FwSizeStoreType" => requireIntegerTypeAlias(a, name, id)
      case "FwSizeType" => requireUnsignedIntegerTypeAlias(a, name, id)
      case "FwTimeBaseStoreType" => requireIntegerTypeAlias(a, name, id)
      case "FwTimeContextStoreType" => requireIntegerTypeAlias(a, name, id)
      case "FwTlmPacketizeIdType" => requireIntegerTypeAlias(a, name, id)
      case "FwTraceIdType" => requireIntegerTypeAlias(a, name, id)
      case _ => Right(a)
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

  private def updateConstants(a: Analysis, name: String, s: Symbol.Constant) = {
    def update(f: FrameworkDefinitions.Constants => FrameworkDefinitions.Constants) = {
      val defs = a.frameworkDefinitions
      val constants = defs.constants
      val constants1 = f(constants)
      val defs1 = defs.copy(constants = constants1)
      a.copy(frameworkDefinitions = defs1)
    }
    name match {
      case "FW_ASSERT_COUNT_MAX" => update(_.copy(fwAssertCountMax = Some(s)))
      case "FW_CMD_ARG_BUFFER_MAX_SIZE" => update(_.copy(fwCmdArgBufferMaxSize = Some(s)))
      case "FW_CMD_STRING_MAX_SIZE" => update(_.copy(fwCmdStringMaxSize = Some(s)))
      case "FW_COM_BUFFER_MAX_SIZE" => update(_.copy(fwComBufferMaxSize = Some(s)))
      case "FW_CONTEXT_DONT_CARE" => update(_.copy(fwContextDontCare = Some(s)))
      case "FW_FILE_BUFFER_MAX_SIZE" => update(_.copy(fwFileBufferMaxSize = Some(s)))
      case "FW_FIXED_LENGTH_STRING_SIZE" => update(_.copy(fwFixedLengthStringSize = Some(s)))
      case "FW_INTERNAL_INTERFACE_STRING_MAX_SIZE" => update(_.copy(fwInternalInterfaceStringMaxSize = Some(s)))
      case "FW_LOG_BUFFER_MAX_SIZE" => update(_.copy(fwLogBufferMaxSize = Some(s)))
      case "FW_LOG_STRING_MAX_SIZE" => update(_.copy(fwLogStringMaxSize = Some(s)))
      case "FW_LOG_TEXT_BUFFER_SIZE" => update(_.copy(fwLogTextBufferSize = Some(s)))
      case "FW_OBJ_SIMPLE_REG_BUFF_SIZE" => update(_.copy(fwObjSimpleRegBuffSize = Some(s)))
      case "FW_OBJ_SIMPLE_REG_ENTRIES" => update(_.copy(fwObjSimpleRegEntries = Some(s)))
      case "FW_PARAM_BUFFER_MAX_SIZE" => update(_.copy(fwParamBufferMaxSize = Some(s)))
      case "FW_PARAM_STRING_MAX_SIZE" => update(_.copy(fwParamStringMaxSize = Some(s)))
      case "FW_QUEUE_NAME_BUFFER_SIZE" => update(_.copy(fwQueueNameBufferSize = Some(s)))
      case "FW_QUEUE_SIMPLE_QUEUE_ENTRIES" => update(_.copy(fwQueueSimpleQueueEntries = Some(s)))
      case "FW_SERIALIZE_FALSE_VALUE" => update(_.copy(fwSerializeFalseValue = Some(s)))
      case "FW_SERIALIZE_TRUE_VALUE" => update(_.copy(fwSerializeTrueValue = Some(s)))
      case "FW_SM_SIGNAL_BUFFER_MAX_SIZE" => update(_.copy(fwSmSignalBufferMaxSize = Some(s)))
      case "FW_STATEMENT_ARG_BUFFER_MAX_SIZE" => update(_.copy(fwStatementArgBufferMaxSize = Some(s)))
      case "FW_TASK_NAME_BUFFER_SIZE" => update(_.copy(fwTaskNameBufferSize = Some(s)))
      case "FW_TLM_BUFFER_MAX_SIZE" => update(_.copy(fwTlmBufferMaxSize = Some(s)))
      case "FW_TLM_STRING_MAX_SIZE" => update(_.copy(fwTlmStringMaxSize = Some(s)))
      case "Fw.DpCfg.CONTAINER_USER_DATA_SIZE" => update(_.copy(fwDpCfgContainerDataSize = Some(s)))
      case _ => a
    }
  }

  private def updateTypes(a: Analysis, name: String, s: TypeSymbol) = {
    def update(f: FrameworkDefinitions.Types => FrameworkDefinitions.Types) = {
      val defs = a.frameworkDefinitions
      val types = defs.types
      val types1 = f(types)
      val defs1 = defs.copy(types = types1)
      a.copy(frameworkDefinitions = defs1)
    }
    name match {
      case "Fw.DpCfg.ProcType" => update(_.copy(fwDpCfgProcType = Some(s)))
      case "Fw.DpState" => update(_.copy(fwDpState = Some(s)))
      case "FwAssertArgType" => update(_.copy(fwAssertArgType = Some(s)))
      case "FwChanIdType" => update(_.copy(fwChanIdType = Some(s)))
      case "FwDpIdType" => update(_.copy(fwDpIdType = Some(s)))
      case "FwDpPriorityType" => update(_.copy(fwDpPriorityType = Some(s)))
      case "FwEnumStoreType" => update(_.copy(fwEnumStoreType = Some(s)))
      case "FwEventIdType" => update(_.copy(fwEventIdType = Some(s)))
      case "FwIndexType" => update(_.copy(fwIndexType = Some(s)))
      case "FwOpcodeType" => update(_.copy(fwOpcodeType = Some(s)))
      case "FwPacketDescriptorType" => update(_.copy(fwPacketDescriptorType = Some(s)))
      case "FwPrmIdType" => update(_.copy(fwPrmIdType = Some(s)))
      case "FwQueuePriorityType" => update(_.copy(fwQueuePriorityType = Some(s)))
      case "FwSignedSizeType" => update(_.copy(fwSignedSizeType = Some(s)))
      case "FwSizeStoreType" => update(_.copy(fwSizeStoreType = Some(s)))
      case "FwSizeType" => update(_.copy(fwSizeType = Some(s)))
      case "FwTaskPriorityType" => update(_.copy(fwTaskPriorityType = Some(s)))
      case "FwTimeBaseStoreType" => update(_.copy(fwTimeBaseStoreType = Some(s)))
      case "FwTimeContextStoreType" => update(_.copy(fwTimeContextStoreType = Some(s)))
      case "FwTlmPacketizeIdType" => update(_.copy(fwTlmPacketizeIdType = Some(s)))
      case "FwTraceIdType" => update(_.copy(fwTraceIdType = Some(s)))
      case _ => a
    }
  }

}
