package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check F Prime framework definitions */
object CheckFrameworkDefs
  extends Analyzer
  with ComponentAnalyzer
  with ModuleAnalyzer
{

  private def updateFrameworkTypes(
    a: Analysis,
    f: FrameworkTypes => FrameworkTypes
  ): Analysis =
    a.copy(
      frameworkDefinitions =
        a.frameworkDefinitions.copy(
          types = f(a.frameworkDefinitions.types)
        )
    )

  private def updateFrameworkConstants(
    a: Analysis,
    f: FrameworkConstants => FrameworkConstants
  ): Analysis =
    a.copy(
      frameworkDefinitions =
        a.frameworkDefinitions.copy(
          constants = f(a.frameworkDefinitions.constants)
        )
    )

  override def defAbsTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAbsType]]) = {
    val name = a.getQualifiedName (Symbol.AbsType(aNode)).toString
    val id = aNode._2.id
    checkFrameworkTypes(a, name, id)
  }

  override def defAliasTypeAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefAliasType]]) = {
    val name = a.getQualifiedName (Symbol.AliasType(aNode)).toString
    val id = aNode._2.id
    for {
      a <- checkFrameworkTypes(a, name, id)
      a <- name match {
        case "FwAssertArgType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwAssertArgType = Some(aNode)))
        case "FwChanIdType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwChanIdType = Some(aNode)))
        case "FwDpIdType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwDpIdType = Some(aNode)))
        case "FwDpPriorityType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwDpPriorityType = Some(aNode)))
        case "FwEnumStoreType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwEnumStoreType = Some(aNode)))
        case "FwEventIdType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwEventIdType = Some(aNode)))
        case "FwIndexType" =>
          for {
            a <- requireSignedIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwIndexType = Some(aNode)))
        case "FwOpcodeType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwOpcodeType = Some(aNode)))
        case "FwPacketDescriptorType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwPacketDescriptorType = Some(aNode)))
        case "FwPrmIdType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwPrmIdType = Some(aNode)))
        case "FwQueuePriorityType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwQueuePriorityType = Some(aNode)))
        case "FwSignedSizeType" =>
          for {
            a <- requireSignedIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwSignedSizeType = Some(aNode)))
        case "FwSizeStoreType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwSizeStoreType = Some(aNode)))
        case "FwSizeType" =>
          for {
            a <- requireUnsignedIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwSizeType = Some(aNode)))
        case "FwTaskPriorityType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwTaskPriorityType = Some(aNode)))
        case "FwTimeBaseStoreType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwTimeBaseStoreType = Some(aNode)))
        case "FwTimeContextStoreType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwTimeContextStoreType = Some(aNode)))
        case "FwTlmPacketizeIdType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwTlmPacketizeIdType = Some(aNode)))
        case "FwTraceIdType" =>
          for {
            a <- requireIntegerTypeAlias(a, name, id)
          } yield updateFrameworkTypes(a, _.copy(fwTraceIdType = Some(aNode)))
        case _ => Right(a)
      }
    } yield a
  }

  override def defArrayAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefArray]]) = {
    val name = a.getQualifiedName (Symbol.Array(aNode)).toString
    val id = aNode._2.id
    checkFrameworkTypes(a, name, id)
  }

  override def defEnumAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefEnum]]) = {
    val name = a.getQualifiedName (Symbol.Enum(aNode)).toString
    val id = aNode._2.id
    name match {
      case "Fw.DpState" => 
        for {
          a <- requireEnum(a, name, id)
        } yield updateFrameworkTypes(a, _.copy(dpState = Some(aNode)))
      case "Fw.DpCfg.ProcType" => 
        for {
          a <- requireEnum(a, name, id)
        } yield updateFrameworkTypes(a, _.copy(procType = Some(aNode)))
      case _ => Right(a)
    }
  }

  override def defStructAnnotatedNode(a: Analysis, aNode: Ast.Annotated[AstNode[Ast.DefStruct]]) = {
    val name = a.getQualifiedName (Symbol.Struct(aNode)).toString
    val id = aNode._2.id
    checkFrameworkTypes(a, name, id)
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
    val name = a.getQualifiedName (Symbol.Constant(aNode)).toString
    val id = aNode._2.id
    name match {
      case "Fw.DpCfg.CONTAINER_USER_DATA_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(containerDataSize = Some(aNode)))
      case "FW_OBJ_SIMPLE_REG_BUFF_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwObjSimpleRegBuffSize = Some(aNode)))
      case "FW_QUEUE_NAME_BUFFER_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwQueueNameBufferSize = Some(aNode)))
      case "FW_TASK_NAME_BUFFER_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwTaskNameBufferSize = Some(aNode)))
      case "FW_COM_BUFFER_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwComBufferMaxSize = Some(aNode)))
      case "FW_SM_SIGNAL_BUFFER_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwSmSignalBufferMaxSize = Some(aNode)))
      case "FW_CMD_ARG_BUFFER_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwCmdArgBufferMaxSize = Some(aNode)))
      case "FW_CMD_STRING_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwCmdStringMaxSize = Some(aNode)))
      case "FW_LOG_BUFFER_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwLogBufferMaxSize = Some(aNode)))
      case "FW_LOG_STRING_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwLogStringMaxSize = Some(aNode)))
      case "FW_TLM_BUFFER_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwTlmBufferMaxSize = Some(aNode)))
      case "FW_STATEMENT_ARG_BUFFER_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwStatementArgBufferMaxSize = Some(aNode)))
      case "FW_TLM_STRING_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwTlmStringMaxSize = Some(aNode)))
      case "FW_PARAM_BUFFER_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwParamBufferMaxSize = Some(aNode)))
      case "FW_PARAM_STRING_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwParamStringMaxSize = Some(aNode)))
      case "FW_FILE_BUFFER_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwFileBufferMaxSize = Some(aNode)))
      case "FW_INTERNAL_INTERFACE_STRING_MAX_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwInternalInterfaceStringMaxSize = Some(aNode)))
      case "FW_LOG_TEXT_BUFFER_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwLogTextBufferSize = Some(aNode)))
      case "FW_FIXED_LENGTH_STRING_SIZE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwFixedLengthStringSize = Some(aNode)))
      case "FW_OBJ_SIMPLE_REG_ENTRIES" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwObjSimpleRegEntries = Some(aNode)))
      case "FW_QUEUE_SIMPLE_QUEUE_ENTRIES" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwQueueSimpleQueueEntries = Some(aNode)))
      case "FW_ASSERT_COUNT_MAX" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwAssertCountMax = Some(aNode)))
      case "FW_CONTEXT_DONT_CARE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwContextDontCare = Some(aNode)))
      case "FW_SERIALIZE_TRUE_VALUE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwSerializeTrueValue = Some(aNode)))
      case "FW_SERIALIZE_FALSE_VALUE" =>
        for {
          a <- requireIntegerConstant(a, name, id)
        } yield updateFrameworkConstants(a, _.copy(fwSerializeFalseValue = Some(aNode)))

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
}
