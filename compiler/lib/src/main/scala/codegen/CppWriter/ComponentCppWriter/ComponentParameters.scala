package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component parameters */
case class ComponentParameters (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  val paramBufferName = "m___fprime_ac_paramBuffer"

  def getConstantMembers: List[CppDoc.Class.Member] =
    guardedList (hasParameters) (List(getParamIds))

  def getPublicFunctionMembers: List[CppDoc.Class.Member] =
    guardedList (hasParameters) (getLoadFunction)

  def getProtectedFunctionMembers: List[CppDoc.Class.Member] =
    guardedList (hasParameters) (
      List.concat(
        getHookFunctions,
        getGetterFunctions,
        getExternalParameterFunctions
      )
    )

  def getPrivateFunctionMembers: List[CppDoc.Class.Member] =
    guardedList (hasParameters) (
      List.concat(
        getSetters,
        getSaveFunctions
      )
    )

  def getVariableMembers: List[CppDoc.Class.Member] =
    List.concat(
      getValidityFlags,
      getParamVars,
      guardedList (hasExternalParameters) (getParamDelegate)
    )

  private def checkValidityFlag(param: Param, flagValue: String) =
    val paramName = param.getName
    val validityFlagName = paramValidityFlagName(paramName)
    s"this->$validityFlagName == Fw::ParamValid::$flagValue"

  private def checkValidityFlagValidOrDefault(param: Param) =
    s"(${checkValidityFlag(param, "VALID")}) || (${checkValidityFlag(param, "DEFAULT")})"

  private def deserializeParam(param: Param) = {
    val paramName = param.getName
    val varName = paramVariableName(paramName)
    val validityFlagName = paramValidityFlagName(paramName)
    if param.isExternal
    then
      val idConstantName = paramIdConstantName(paramName)
      lines(
        s"""|FW_ASSERT(this->paramDelegatePtr != nullptr);
            |_stat = this->paramDelegatePtr->deserializeParam(
            |  _baseId,
            |  $idConstantName,
            |  this->$validityFlagName,
            |  this->$paramBufferName
            |);"""
      )
    else
      lines(s"_stat = this->$paramBufferName.deserializeTo(this->$varName);")
  }

  private def getExternalParameterFunctions: List[CppDoc.Class.Member] = {
    lazy val delegateInit = addAccessTagAndComment(
      "protected",
      "External parameter delegate initialization",
      List(
        functionClassMember(
          Some("Initialize the external parameter delegate"),
          "registerExternalParameters",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("Fw::ParamExternalDelegate*"),
              "paramExternalDelegatePtr",
              Some("The delegate for externally managed parameters")
            )
          ),
          CppDoc.Type("void"),
          lines(
            """|FW_ASSERT(paramExternalDelegatePtr != nullptr);
               |this->paramDelegatePtr = paramExternalDelegatePtr;
               |"""
          )
        )
      )
    )
    guardedList (hasExternalParameters) (delegateInit)
  }

  private def getGetterFunctionForParam(param: Param) =
    functionClassMember(
      Some(
        addSeparatedString(
          s"Get parameter ${param.getName}\n\n\\return The parameter value",
          AnnotationCppWriter.asStringOpt(param.aNode)
        )
      ),
      paramGetterName(param.getName),
      List(
        CppDoc.Function.Param(
          CppDoc.Type("Fw::ParamValid&"),
          "valid",
          Some("Whether the parameter is valid")
        )
      ),
      CppDoc.Type(writeParamType(param.paramType, "Fw::ParamString")),
      writeGetterFunctionBody(param)
    )

  private def getGetterFunctions: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "protected",
      "Parameter get functions",
      sortedParams.map((_, param) => getGetterFunctionForParam(param))
    )

  private def getHookFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Parameter hook functions",
      List(
        getParamUpdateHookFunction,
        getParamLoadHookFunction
      )
    )
  }

  private def getLoadFunction: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      "Parameter loading",
      List(
        functionClassMember(
          Some(
            s"""|\\brief Load the parameters from a parameter source
                |
                |Connect the parameter first
                |"""
          ),
          "loadParameters",
          Nil,
          CppDoc.Type("void"),
          writeLoadFunctionBody
        )
      )
    )

  private def getParamBuffer =
    linesClassMember(
        lines(
          s"""|
              |//! Scratch buffer for parameter management
              |Fw::ParamBuffer $paramBufferName;"""
        )
    )

  private def getParamDelegate =
    addAccessTagAndComment(
      "private",
      "Parameter delegate",
      List(
        linesClassMember(
          lines(
            s"""|
                |//! Delegate to serialize/deserialize an externally stored parameter
                |Fw::ParamExternalDelegate* paramDelegatePtr = nullptr;
                |"""
          )
        )
      ),
      CppDoc.Lines.Hpp
    )

  private def getParamFromComponent(param: Param) = {
    if param.isExternal
    then
      val idConstantName = paramIdConstantName(param.getName)
      lines(
        s"""|$paramBufferName.resetSer();
            |FW_ASSERT(this->paramDelegatePtr != nullptr);
            |Fw::SerializeStatus _stat = this->paramDelegatePtr->serializeParam(
            |  static_cast<FwPrmIdType>(this->getIdBase()),
            |  $idConstantName,
            |  $paramBufferName
            |);
            |if(_stat == Fw::FW_SERIALIZE_OK) {
            |  _stat = $paramBufferName.deserializeTo(_local);
            |  FW_ASSERT(_stat == Fw::FW_SERIALIZE_OK, static_cast<FwAssertArgType>(_stat));
            |} else {
            |  valid = Fw::ParamValid::INVALID;
            |}"""
      )
    else
      val variableName = paramVariableName(param.getName)
      lines(s"_local = this->$variableName;")
  }

  private def getParamFromPort(param: Param) = {
    val paramName = param.getName
    val idConstantName = paramIdConstantName(paramName)
    val prmGetPortInvokerName = outputPortInvokerName(prmGetPort.get)
    val validityFlagName = paramValidityFlagName(param.getName)
    lines(
      s"""|
          |_id = _baseId + $idConstantName;
          |
          |// Get serialized parameter $paramName
          |this->$validityFlagName = this->$prmGetPortInvokerName(
          |  0,
          |  _id,
          |  this->$paramBufferName
          |);"""
    )
  }

  private def getParamIds = linesClassMember(
    List.concat(
      Line.blank :: lines(s"//! Parameter IDs"),
      wrapInEnum(sortedParams.flatMap(writeParamIdConstant))
    )
  )

  private def getParamLoadHookFunction = functionClassMember(
    Some(
      s"""|\\brief Called whenever parameters are loaded
          |
          |This function does nothing by default. You may override it.
          |"""
    ),
    "parametersLoaded",
    Nil,
    CppDoc.Type("void"),
    lines("// Do nothing by default"),
    CppDoc.Function.Virtual
  )

  private def getParamUpdateHookFunction = functionClassMember(
    Some(
      s"""|\\brief Called whenever a parameter is updated
          |
          |This function does nothing by default. You may override it.
          |"""
    ),
    "parameterUpdated",
    List(
      CppDoc.Function.Param(
        CppDoc.Type("FwPrmIdType"),
        "id",
        Some("The parameter ID")
      )
    ),
    CppDoc.Type("void"),
    lines("// Do nothing by default"),
    CppDoc.Function.Virtual
  )

  private def getParamVarForParam(param: Param) =
    guardedList (!param.isExternal) {
      val paramType = writeParamType(param.paramType, "Fw::ParamString")
      val paramVarName = paramVariableName(param.getName)
      List(
        linesClassMember(
          List.concat(
            addSeparatedPreComment(
              s"Parameter ${param.getName}",
              AnnotationCppWriter.asStringOpt(param.aNode)
            ),
            lines(s"$paramType $paramVarName;")
          )
        )
      )
    }

  private def getParamVars = addAccessTagAndComment(
    "private",
    "Parameter variables",
    List.concat(
      guardedList(!sortedParams.isEmpty) (List(getParamBuffer)),
      sortedParams.flatMap((_, param) => getParamVarForParam(param))
    ),
    CppDoc.Lines.Hpp
  )

  private def getSaveFunctionForParam(param: Param) =
    functionClassMember(
      Some(
        s"""|Save parameter ${param.getName}
            |
            |\\return The command response
            |"""
      ),
      paramHandlerName(param.getName, Command.Param.Save),
      Nil,
      CppDoc.Type("Fw::CmdResponse"),
      writeSaveFunctionBodyForParam(param)
    )

  private def getSaveFunctions: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "private",
      "Parameter save functions",
      sortedParams.map((_, param) => getSaveFunctionForParam(param))
    )

  private def getSetterForParam(param: Param) =
    functionClassMember(
      Some(
        s"""|Set parameter ${param.getName}
            |
            |\\return The command response
            |"""
      ),
      paramHandlerName(param.getName, Command.Param.Set),
      List(
        CppDoc.Function.Param(
          CppDoc.Type("Fw::SerialBufferBase&"),
          "val",
          Some("The serialization buffer")
        ),
      ),
      CppDoc.Type("Fw::CmdResponse"),
      if (param.isExternal)
      then writeSetterBodyForExternalParam(param)
      else writeSetterBodyForInternalParam(param)
    )

  private def getSetters: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "private",
      "Parameter set functions",
      sortedParams.map((_, param) => getSetterForParam(param))
    )

  private def getValidityFlagForParam(param: Param) = {
    val paramName = param.getName
    val flagName = paramValidityFlagName(paramName)
    linesClassMember(
      lines(
        s"""|
            |//! VALID if $paramName was successfully received
            |Fw::ParamValid $flagName = Fw::ParamValid::UNINIT;
            |"""
      )
    )
  }

  private def getValidityFlags = addAccessTagAndComment(
    "private",
    "Parameter validity flags",
    sortedParams.map((_, param) => getValidityFlagForParam(param)),
    CppDoc.Lines.Hpp
  )

  private def paramGetterName(name: String) =
    s"paramGet_$name"

  private def paramVariableName(name: String) =
    s"m_$name"

  private def setDefaultValue(param: Param, value: Value) = {
    val paramName = param.getName
    val varName = paramVariableName(paramName)
    val cppValue = ValueCppWriter.write(s, value)
    if param.isExternal
    then
      val cppType = TypeCppWriter.getName(s, value.getType, "Fw::String")
      val validityFlagName = paramValidityFlagName(param.getName)
      List.concat(
        lines(
          s"""|$cppType _val = $cppValue;
              |this->$paramBufferName.resetSer();
              |_stat = this->$paramBufferName.serializeFrom(_val);"""
        ),
        deserializeParam(param),
        wrapInIf(
          "_stat != Fw::FW_SERIALIZE_OK",
          lines(s"this->$validityFlagName = Fw::ParamValid::INVALID;")
        )
      )
    else
      lines(s"this->$varName = $cppValue;")
  }

  private def setValidityFlag(param: Param, flagValue: String) =
    val paramName = param.getName
    val validityFlagName = paramValidityFlagName(paramName)
    s"this->$validityFlagName = Fw::ParamValid::$flagValue;"

  private def setValidityFlagLines(param: Param, flagValue: String) =
    lines(setValidityFlag(param, flagValue))

  private def writeGetterFunctionBody(param: Param) = {
    val paramType = writeParamType(param.paramType, "Fw::ParamString")
    val validityFlagName = paramValidityFlagName(param.getName)
    List.concat(
      lines(
        s"""|$paramType _local{};
            |this->m_paramLock.lock();
            |valid = this->$validityFlagName;"""
      ),
      wrapInIf(
        "(valid == Fw::ParamValid::VALID) || (valid == Fw::ParamValid::DEFAULT)",
        getParamFromComponent(param)
      ),
      lines(
        """|this->m_paramLock.unLock();
           |return _local;"""
      )
    )
  }

  private def writeLoadForParam(param: Param) = {
    // Generate a block, or an if statement, or an if-else statement
    def writeCondition(
      condition: => String,
      ifBlock: List[Line],
      elseBlock: => List[Line]
    ) =
      if param.isExternal && !param.default.isDefined
      // External parameter, no default: no condition needed
      then ifBlock
      else if param.default.isDefined
      // Default: if and else needed
      then wrapInIfElse(condition, ifBlock, elseBlock)
      // Internal parameter, no default: if needed
      else wrapInIf(condition, ifBlock)
    List.concat(
      getParamFromPort(param),
      {
        val orUseDefaultValue = param.default match {
          case Some(_) => " or use default value"
          case None => ""
        }
        lines(
          s"""|
              |this->m_paramLock.lock();
              |
              |// Deserialize parameter$orUseDefaultValue"""
        )
      },
      writeCondition(
        checkValidityFlag(param, "VALID"),
        List.concat(
          deserializeParam(param),
          wrapInIf(
            "_stat != Fw::FW_SERIALIZE_OK",
            setValidityFlagLines(
              param,
              if param.default.isDefined then "DEFAULT" else "INVALID"
            )
          )
        ),
        setValidityFlagLines(param, "DEFAULT")
      ),
      param.default match {
        case Some(value) =>
          wrapInIf(
            checkValidityFlag(param, "DEFAULT"),
            setDefaultValue(param, value)
          )
        case None => Nil
      },
      lines(
        """|
           |this->m_paramLock.unLock();"""
      )
    )
  }

  private def writeLoadFunctionBody = {
    val prmGetPortName = prmGetPort.get.getUnqualifiedName
    val prmGetIsConnected = outputPortIsConnectedName(prmGetPortName)
    List.concat(
      lines(
        s"""|Fw::SerializeStatus _stat = Fw::FW_SERIALIZE_OK;
            |const FwPrmIdType _baseId = static_cast<FwPrmIdType>(this->getIdBase());
            |FW_ASSERT(this->$prmGetIsConnected(0));
            |
            |FwPrmIdType _id{};
            |"""
      ),
      sortedParams.flatMap((_, param) => writeLoadForParam(param)),
      lines(
        """|
           |// Call notifier
           |this->parametersLoaded();
           |"""
      )
    )
  }

  private def writeParamIdConstant(
    id: Param.Id,
    param: Param
  ) = writeEnumConstant(
    paramIdConstantName(param.getName),
    id,
    AnnotationCppWriter.asStringOpt(param.aNode),
    CppWriterUtils.Hex
  )

  private def writeSaveFunctionBodyForParam(param: Param) = {
    val idConstantName = paramIdConstantName(param.getName)
    val paramVarName = paramVariableName(param.getName)
    val prmSetPortName = prmSetPort.get.getUnqualifiedName
    val prmSetIsConnected = outputPortIsConnectedName(prmSetPortName)
    val prmSetPortInvokerName = outputPortInvokerName(prmSetPort.get)
    List.concat(
      lines(
        s"""|if (!this->$prmSetIsConnected(0)) {
            |  return Fw::CmdResponse::EXECUTION_ERROR;
            |}
            |const FwIdType idBase = this->getIdBase();
            |Fw::SerializeStatus _stat = Fw::FW_SERIALIZE_FORMAT_ERROR;
            |// Serialize the parameter
            |this->m_paramLock.lock();
            |this->$paramBufferName.resetSer();"""
      ),
      wrapInIf(
        checkValidityFlagValidOrDefault(param),
        if (param.isExternal)
        then lines(
          s"""|FW_ASSERT(this->paramDelegatePtr != nullptr);
              |_stat = this->paramDelegatePtr->serializeParam(
              |  static_cast<FwPrmIdType>(idBase),
              |  $idConstantName,
              |  this->$paramBufferName
              |);"""
        )
        else lines (
          s"_stat = this->$paramBufferName.serializeFrom($paramVarName);"
        )
      ),
      lines(
        s"""|this->m_paramLock.unlock();
            |if (_stat != Fw::FW_SERIALIZE_OK) {
            |  return Fw::CmdResponse::VALIDATION_ERROR;
            |}
            |// Save the parameter
            |this->$prmSetPortInvokerName(
            |  0,
            |  static_cast<FwPrmIdType>(idBase + $idConstantName),
            |  this->$paramBufferName
            |);
            |// Return the command response
            |return Fw::CmdResponse::OK;"""
      )
    )
  }

  private def writeSetterBodyForExternalParam(param: Param) = {
    val idConstantName = paramIdConstantName(param.getName)
    lines(
      s"""|Fw::CmdResponse _response{};
          |
          |this->m_paramLock.lock();
          |// Update the external parameter
          |FW_ASSERT(this->paramDelegatePtr != nullptr);
          |const Fw::SerializeStatus _stat = this->paramDelegatePtr->deserializeParam(
          |  static_cast<FwPrmIdType>(this->getIdBase()),
          |  $idConstantName,
          |  Fw::ParamValid::VALID,
          |  val
          |);
          |// Set response and update component state
          |if (_stat == Fw::FW_SERIALIZE_OK) {
          |  ${setValidityFlag(param, "VALID")}
          |  _response = Fw::CmdResponse::OK;
          |}
          |else {
          |  ${setValidityFlag(param, "INVALID")}
          |  _response = Fw::CmdResponse::VALIDATION_ERROR;
          |}
          |this->m_paramLock.unLock();
          |
          |// Call notifier
          |this->parameterUpdated($idConstantName);
          |return _response;"""
    )
  }

  private def writeSetterBodyForInternalParam(param: Param) = {
    val paramType = writeParamType(param.paramType, "Fw::ParamString")
    val varName = paramVariableName(param.getName)
    val validityFlagName = paramValidityFlagName(param.getName)
    val idConstantName = paramIdConstantName(param.getName)
    lines(
      s"""|$paramType _localVal{};
          |const Fw::SerializeStatus _stat = val.deserializeTo(_localVal);
          |if (_stat != Fw::FW_SERIALIZE_OK) {
          |  return Fw::CmdResponse::VALIDATION_ERROR;
          |}
          |
          |// Assign value only if successfully deserialized
          |this->m_paramLock.lock();
          |this->$varName = _localVal;
          |this->$validityFlagName = Fw::ParamValid::VALID;
          |this->m_paramLock.unLock();
          |
          |// Call notifier
          |this->parameterUpdated($idConstantName);
          |return Fw::CmdResponse::OK;
          |"""
    )
  }

}
