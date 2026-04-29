package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check F Prime framework constant values */
object CheckFrameworkConstantValues {

  def check(a: Analysis) =
    Result.foldLeft (a.frameworkDefinitions.constantMap.toList) (a) (checkMapEntry)

  private def checkMapEntry(a: Analysis, entry: (String, Symbol.Constant)) =
    val (name, s) = entry
    checkers.get(name).map(_(a, name, s)).getOrElse(Right(a))

  private def requireNonnegativeIntegerConstant(a: Analysis, name: String, s: Symbol.Constant) =
    val v = a.getBigIntValue(s.getNodeId)
    if (v >= 0) then Right(a) else 
      val valueId = s.node._2.data.value.id
      Left(
        SemanticError.InvalidIntValue(
          Locations.get(valueId),
          v,
          s"framework definition $name must be a nonnegative integer constant"
        )
      )

  private def requirePositiveIntegerConstant(a: Analysis, name: String, s: Symbol.Constant) =
    val v = a.getBigIntValue(s.getNodeId)
    if (v > 0) then Right(a) else 
      val valueId = s.node._2.data.value.id
      Left(
        SemanticError.InvalidIntValue(
          Locations.get(valueId),
          v,
          s"framework definition $name must be a positive integer constant"
        )
      )

  private def requireStringSizeConstant(a: Analysis, name: String, s: Symbol.Constant) =
    val v = a.getBigIntValue(s.getNodeId)
    if (Type.String.isValidSize(v)) then Right(a) else
      val valueId = s.node._2.data.value.id
      val error = Left(
        SemanticError.InvalidStringSize(
          Locations.get(valueId),
          v
        )
      )
      val annotation = s"framework definition $name must be a string size constant"
      Result.annotateResult(error, annotation)

  private val checkers = Map(
    "FW_ASSERT_COUNT_MAX" -> requireNonnegativeIntegerConstant,
    "FW_CMD_ARG_BUFFER_MAX_SIZE" -> requirePositiveIntegerConstant,
    "FW_CMD_STRING_MAX_SIZE" -> requireStringSizeConstant,
    "FW_COM_BUFFER_MAX_SIZE" -> requirePositiveIntegerConstant,
    "FW_FILE_BUFFER_MAX_SIZE" -> requirePositiveIntegerConstant,
    "FW_FIXED_LENGTH_STRING_SIZE" -> requireStringSizeConstant,
    "FW_INTERNAL_INTERFACE_STRING_MAX_SIZE" -> requireStringSizeConstant,
    "FW_LOG_BUFFER_MAX_SIZE" -> requirePositiveIntegerConstant,
    "FW_LOG_STRING_MAX_SIZE" -> requireStringSizeConstant,
    "FW_LOG_TEXT_BUFFER_SIZE" -> requirePositiveIntegerConstant,
    "FW_OBJ_SIMPLE_REG_BUFF_SIZE" -> requirePositiveIntegerConstant,
    "FW_OBJ_SIMPLE_REG_ENTRIES" -> requirePositiveIntegerConstant,
    "FW_PARAM_BUFFER_MAX_SIZE" -> requirePositiveIntegerConstant,
    "FW_PARAM_STRING_MAX_SIZE" -> requireStringSizeConstant,
    "FW_QUEUE_NAME_BUFFER_SIZE" -> requirePositiveIntegerConstant,
    "FW_QUEUE_SIMPLE_QUEUE_ENTRIES" -> requirePositiveIntegerConstant,
    "FW_SM_SIGNAL_BUFFER_MAX_SIZE" -> requirePositiveIntegerConstant,
    "FW_STATEMENT_ARG_BUFFER_MAX_SIZE" -> requirePositiveIntegerConstant,
    "FW_TASK_NAME_BUFFER_SIZE" -> requirePositiveIntegerConstant,
    "FW_TLM_BUFFER_MAX_SIZE" -> requirePositiveIntegerConstant,
    "FW_TLM_STRING_MAX_SIZE" -> requireStringSizeConstant,
    "Fw.DpCfg.CONTAINER_USER_DATA_SIZE" -> requirePositiveIntegerConstant
  )

}
