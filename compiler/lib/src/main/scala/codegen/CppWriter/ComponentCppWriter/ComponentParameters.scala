package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._

/** Writes out C++ for component parameters */
case class ComponentParameters (
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  def getConstantMembers: List[CppDoc.Class.Member] = {
    if !hasParameters then Nil
    else List(
      linesClassMember(
        List(
          Line.blank :: lines(s"//! Parameter IDs"),
          wrapInEnum(
            lines(
              sortedParams.map((id, param) =>
                writeEnumConstant(
                  paramIdConstantName(param.getName),
                  id,
                  AnnotationCppWriter.asStringOpt(param.aNode),
                  CppWriterUtils.Hex
                )
              ).mkString("\n")
            )
          )
        ).flatten
      )
    )
  }

  def getPublicFunctionMembers: List[CppDoc.Class.Member] = {
    if !hasParameters then Nil
    else getLoadFunction
  }

  def getProtectedFunctionMembers: List[CppDoc.Class.Member] = {
    if !hasParameters then Nil
    else List(
      getHookFunctions,
      getGetterFunctions
    ).flatten
  }

  def getPrivateFunctionMembers: List[CppDoc.Class.Member] = {
    if !hasParameters then Nil
    else List(
      getPrivateGetter,
      getSetters,
      getSaveFunctions
    ).flatten
  }

  def getVariableMembers: List[CppDoc.Class.Member] = {
    if !hasParameters then Nil
    else List(
      addAccessTagAndComment(
        "PRIVATE",
        "Parameter validity flags",
        sortedParams.map((_, param) =>
          linesClassMember(
            lines(
              s"""|
                  |//! True if ${param.getName} was successfully received
                  |Fw::ParamValid ${paramValidityFlagName(param.getName)};
                  |"""
            )
          )
        ),
        CppDoc.Lines.Hpp
      ),
      addAccessTagAndComment(
        "PRIVATE",
        "Parameter variables",
        sortedParams.map((_, param) =>
          linesClassMember(
            Line.blank :: lines(
              addSeparatedPreComment(
                s"Parameter ${param.getName}",
                AnnotationCppWriter.asStringOpt(param.aNode)
              ) +
                s"\n${writeParamType(param.paramType)} ${paramVariableName(param.getName)};"
            )
          )
        ),
        CppDoc.Lines.Hpp
      )
    ).flatten
  }

  private def getLoadFunction: List[CppDoc.Class.Member] = {
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
          Nil
        )
      )
    )
  }

  private def getHookFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      "Parameter update hook",
      List(
        functionClassMember(
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
          Nil,
          CppDoc.Function.Virtual
        ),
        linesClassMember(
          CppDocWriter.writeBannerComment(
            "Parameter load hook"
          )
        ),
        functionClassMember(
          Some(
            s"""|\\brief Called whenever parameters are loaded
                |
                |This function does nothing by default. You may override it.
                |"""
          ),
          "parametersLoaded",
          Nil,
          CppDoc.Type("void"),
          Nil,
          CppDoc.Function.Virtual
        )
      )
    )
  }

  private def getGetterFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PROTECTED",
      "Parameter get functions",
      sortedParams.map((_, param) =>
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
              "isValid",
              Some("Whether the parameter is valid")
            )
          ),
          CppDoc.Type(writeParamType(param.paramType)),
          Nil
        )
      )
    )
  }

  private def getPrivateGetter: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PRIVATE",
      "Private parameter get function",
      List(
        functionClassMember(
          Some(
            """|Get a parameter by ID
               |
               |\return Whether the parameter is valid
               |"""
          ),
          "getParam",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("FwPrmIdType"),
              "id",
              Some("The ID")
            ),
            CppDoc.Function.Param(
              CppDoc.Type("Fw::ParamBuffer&"),
              "buff",
              Some("The parameter value")
            )
          ),
          CppDoc.Type("Fw::ParamValid"),
          Nil
        )
      )
    )
  }

  private def getSetters: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PRIVATE",
      "Parameter set functions",
      sortedParams.map((_, param) =>
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
              CppDoc.Type("Fw::SerializeBufferBase&"),
              "val",
              Some("The serialization buffer")
            ),
          ),
          CppDoc.Type("Fw::CmdResponse"),
          Nil
        )
      )
    )
  }

  private def getSaveFunctions: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PRIVATE",
      "Parameter save functions",
      sortedParams.map((_, param) =>
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
          Nil
        )
      )
    )
  }

  private def writeParamType(t: Type) =
    TypeCppWriter.getName(s, t, Some("Fw::ParamString"))

  private def paramIdConstantName(name: String) =
    s"PARAMID_${name.toUpperCase}"

  private def paramGetterName(name: String) =
    s"paramGet_$name"

  private def paramVariableName(name: String) =
    s"m_$name"

}