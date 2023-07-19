package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component Google Test harness base classes */
case class ComponentGTestBaseWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  private val fileName = ComputeCppFiles.FileNames.getComponentGTestBase(name)

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name component Google Test harness base class",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers = {
    val cls = classMember(
      Some(
        s"\\class $gTestClassName\n\\brief Auto-generated base for $name component Google Test harness",
      ),
      gTestClassName,
      Some(s"public $testerBaseClassName"),
      getClassMembers
    )
    List.concat(
      List(getHppIncludes, getCppIncludes),
      getMacros,
      wrapInNamespaces(namespaceIdentList, List(cls))
    )
  }

  private def getHppIncludes = {
    val userHeaders = List(
      s"${s.getRelativePath(ComputeCppFiles.FileNames.getComponentTesterBase(name)).toString}.hpp",
      "gtest/gtest.h"
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(
      addBlankPrefix(userHeaders)
    )
  }

  private def getCppIncludes = {
    val userHeader = lines(CppWriter.headerString(s"${s.getRelativePath(fileName).toString}.hpp"))
    linesMember(
      addBlankPrefix(userHeader),
      CppDoc.Lines.Cpp
    )
  }

  private def getMacros = {
    List()
  }

  private def getClassMembers = {
    List.concat(
      getConstructors,
      getPortAssertFunctions,
      getCmdAssertFunctions,
      getTlmAssertFunctions,
    )
  }

  private def getConstructors = {
    addAccessTagAndComment(
      "protected",
      "Construction and destruction",
      List(
        constructorClassMember(
          Some(s"Construct object $gTestClassName"),
          constructorParams,
          List(
            s"$testerBaseClassName(compName, maxHistorySize)"
          ),
          Nil
        ),
        destructorClassMember(
          Some(s"Destroy object $gTestClassName"),
          Nil
        )
      )
    )
  }

  private def getPortAssertFunctions = {
    addAccessTagAndComment(
      "protected",
      "From ports",
      List.concat(
        if typedOutputPorts.nonEmpty then List(
          functionClassMember(
            Some("From ports"),
            "assertFromPortHistory_size",
            sizeAssertionFunctionParams,
            CppDoc.Type("void"),
            lines(
              raw"""ASSERT_EQ(size, this->fromPortHistorySize)
                   |  << "\n"
                   |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
                   |  << "  Value:    Total size of all from port histories\n"
                   |  << "  Expected: " << size << "\n"
                   |  << "  Actual:   " << this->fromPortHistorySize << "\n";
                   |"""
            ),
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        )
        else Nil,
        typedOutputPorts.map(p => {
          val portSize = portParamTypeMap(p.getUnqualifiedName) match {
            case Nil => fromPortHistorySizeName(p.getUnqualifiedName)
            case _ => s"${fromPortHistoryName(p.getUnqualifiedName)}->size()"
          }

          functionClassMember(
            Some(s"From port: ${p.getUnqualifiedName}"),
            fromPortAssertionFuncName(p.getUnqualifiedName),
            sizeAssertionFunctionParams,
            CppDoc.Type("void"),
            lines(
              s"""ASSERT_EQ(size, this->fromPortHistory_typedOut->size())
                 |  << "\\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
                 |  << "  Value:    Size of history for from_typedOut\\n"
                 |  << "  Expected: " << size << "\\n"
                 |  << "  Actual:   " << this->$portSize << "\\n";
                 |"""
            ),
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        })
      )
    )
  }

  private def getCmdAssertFunctions = {
    addAccessTagAndComment(
      "protected",
      "Commands",
      if hasCommands then List(
        functionClassMember(
          Some("Assert size of command response history"),
          "assertCmdResponse_size",
          sizeAssertionFunctionParams,
          CppDoc.Type("void"),
          lines(
            raw"""ASSERT_EQ(size, this->cmdResponseHistory->size())
                 |  << "\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
                 |  << "  Value:    Size of command response history\n"
                 |  << "  Expected: " << size << "\n"
                 |  << "  Actual:   " << this->cmdResponseHistory->size() << "\n";
                 |"""
          ),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        ),
        functionClassMember(
          Some("Assert the command response history at index"),
          "assertCmdResponse",
          assertionFunctionParams ++ List(
            opcodeParam,
            cmdSeqParam,
            CppDoc.Function.Param(
              CppDoc.Type("Fw::CmdResponse"),
              "response",
              Some("The command response")
            )
          ),
          CppDoc.Type("void"),
          lines(
            raw"""ASSERT_LT(__index, this->cmdResponseHistory->size())
                 |  << "\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
                 |  << "  Value:    Index into command response history\n"
                 |  << "  Expected: Less than size of command response history ("
                 |  << this->cmdResponseHistory->size() << ")\n"
                 |  << "  Actual:   " << __index << "\n";
                 |const CmdResponse& e = this->cmdResponseHistory->at(__index);
                 |ASSERT_EQ(opCode, e.opCode)
                 |  << "\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
                 |  << "  Value:    Opcode at index "
                 |  << __index
                 |  << " in command response history\n"
                 |  << "  Expected: " << opCode << "\n"
                 |  << "  Actual:   " << e.opCode << "\n";
                 |ASSERT_EQ(cmdSeq, e.cmdSeq)
                 |  << "\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
                 |  << "  Value:    Command sequence number at index "
                 |  << __index
                 |  << " in command response history\n"
                 |  << "  Expected: " << cmdSeq << "\n"
                 |  << "  Actual:   " << e.cmdSeq << "\n";
                 |ASSERT_EQ(response, e.response)
                 |  << "\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
                 |  << "  Value:    Command response at index "
                 |  << __index
                 |  << " in command response history\n"
                 |  << "  Expected: " << response << "\n"
                 |  << "  Actual:   " << e.response << "\n";
                 |"""
          ),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      )
      else Nil
    )
  }

  private def getEventAssertFunctions = {
    addAccessTagAndComment(
      "protected",
      "Events",
      List.concat(
        if hasEvents then List(
          functionClassMember(
            Some("Assert the size of event history"),
            "assertEvents_size",
            sizeAssertionFunctionParams,
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        )
        else Nil,
        sortedEvents.flatMap((id, event) =>
          functionClassMember(
            Some(s"Event: ${event.getName}"),
            eventSizeAssertionFuncName(event.getName),
            sizeAssertionFunctionParams,
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          ) :: eventParamTypeMap(id) match {
            case Nil => Nil
            case params => List(
              functionClassMember(
                Some(s"Event: ${event.getName}"),
                eventAssertionFuncName(event.getName),
                assertionFunctionParams ++
                  formalParamsCppWriter.write(
                    event.aNode._2.data.params,
                    Nil,
                    Some("Fw::LogStringArg"),
                    FormalParamsCppWriter.Value
                  ),
                CppDoc.Type("void"),
                Nil,
                CppDoc.Function.NonSV,
                CppDoc.Function.Const
              )
            )
          }
        )
      )
    )
  }

  private def getTlmAssertFunctions = {
    addAccessTagAndComment(
      "protected",
      "Telemetry",
      List.concat(
        if hasChannels then List(
          functionClassMember(
            Some("Assert the size of telemetry history"),
            "assertTlm_size",
            sizeAssertionFunctionParams,
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        )
        else Nil,
        sortedChannels.flatMap((_, channel) => List(
          functionClassMember(
            Some(s"Channel: ${channel.getName}"),
            tlmSizeAssertionFuncName(channel.getName),
            sizeAssertionFunctionParams,
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          ),
          functionClassMember(
            Some(s"Channel: ${channel.getName}"),
            tlmAssertionFuncName(channel.getName),
            assertionFunctionParams ++ List(
              CppDoc.Function.Param(
                CppDoc.Type(s"const ${getChannelType(channel.channelType)}&"),
                "val",
                Some("The channel value")
              )
            ),
            CppDoc.Type("void"),
            Nil,
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        ))
      )
    )
  }

}
