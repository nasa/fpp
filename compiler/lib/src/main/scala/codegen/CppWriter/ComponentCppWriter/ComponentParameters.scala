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
                  ComponentCppWriterUtils.Hex
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
      linesClassMember(
        List(
          CppDocHppWriter.writeAccessTag("PRIVATE"),
          CppDocWriter.writeBannerComment(
            "Parameter validity flags"
          ),
          sortedParams.flatMap((_, param) =>
            lines(
              s"""|
                  |//! True if ${param.getName} was successfully received
                  |Fw::ParamValid ${paramValidityFlagName(param.getName)};
                  |"""
            )
          ),
          CppDocHppWriter.writeAccessTag("PRIVATE"),
          CppDocWriter.writeBannerComment(
            "Parameter variables"
          ),
          sortedParams.flatMap((_, param) =>
            Line.blank :: lines(
              addSeparatedPreComment(
                s"Parameter ${param.getName}",
                AnnotationCppWriter.asStringOpt(param.aNode)
              ) +
                s"\n${writeParamType(param.paramType)} ${paramVariableName(param.getName)};"
            )
          ),
        ).flatten
      )
    )
  }

  private def getLoadFunction: List[CppDoc.Class.Member] = {
    List(
      writeAccessTagAndComment(
        "public",
        "Parameter loading"
      ),
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
    ).flatten
  }

  private def getHookFunctions: List[CppDoc.Class.Member] = {
    List(
      writeAccessTagAndComment(
        "PROTECTED",
        "Parameter update hook"
      ),
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
    ).flatten
  }

  private def getGetterFunctions: List[CppDoc.Class.Member] = {
    List(
      writeAccessTagAndComment(
        "PROTECTED",
        "Parameter get functions"
      ),
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
    ).flatten
  }

  private def getPrivateGetter: List[CppDoc.Class.Member] = {
    List(
      writeAccessTagAndComment(
        "PRIVATE",
        "Private parameter get function"
      ),
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
    ).flatten
  }

  private def getSetters: List[CppDoc.Class.Member] = {
    List(
      writeAccessTagAndComment(
        "PRIVATE",
        "Parameter set functions"
      ),
      sortedParams.map((_, param) =>
        functionClassMember(
          Some(
            s"""|Set parameter ${param.getName}
                |
                |\\return The command response
                |"""
          ),
          paramSetterName(param.getName),
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
    ).flatten
  }

  private def getSaveFunctions: List[CppDoc.Class.Member] = {
    List(
      writeAccessTagAndComment(
        "PRIVATE",
        "Parameter save functions"
      ),
      sortedParams.map((_, param) =>
        functionClassMember(
          Some(
            s"""|Save parameter ${param.getName}
                |
                |\\return The command response
                |"""
          ),
          paramSaveName(param.getName),
          Nil,
          CppDoc.Type("Fw::CmdResponse"),
          Nil
        )
      )
    ).flatten
  }

  private def writeParamType(t: Type) =
    writeCppTypeName(t, s, Nil, Some("Fw::ParamString"))

  private def paramIdConstantName(name: String) =
    s"PARAMID_${name.toUpperCase}"

  private def paramGetterName(name: String) =
    s"paramGet_$name"

  private def paramSetterName(name: String) =
    s"paramSet_$name"

  private def paramSaveName(name: String) =
    s"paramSave_$name"

  private def paramVariableName(name: String) =
    s"m_$name"

}