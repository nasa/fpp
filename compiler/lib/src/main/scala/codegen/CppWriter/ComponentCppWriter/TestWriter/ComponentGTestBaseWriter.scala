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
    List(
      getPortMacros,
      getCmdMacros,
      getEventMacros,
      getTlmMacros,
    )
  }

  private def getClassMembers = {
    List.concat(
      getConstructors,
      getPortAssertFunctions,
      getCmdAssertFunctions,
      getEventAssertFunctions,
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

  private def getPortMacros = {
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Macros for typed user from port history assertions"),
        Line.blank :: lines(
          """#define ASSERT_FROM_PORT_HISTORY_SIZE(size) \
            |  this->assertFromPortHistory_size(__FILE__, __LINE__, size)
            |"""
        ),
        typedOutputPorts.flatMap(p => {
          val params = portParamTypeMap(p.getUnqualifiedName)
          val paramList = params.map((name, _) => s", _$name").mkString("")

          List.concat(
            Line.blank :: lines(
              s"""#define ASSERT_from_${p.getUnqualifiedName}_SIZE(size) \\
                 |  this->${fromPortSizeAssertionFuncName(p.getUnqualifiedName)}(__FILE__, __LINE__, size)
                 |"""
            ),
            params match {
              case Nil => Nil
              case _ => Line.blank :: lines(
                s"""#define ASSERT_from_${p.getUnqualifiedName}(index$paramList) \\
                   |  { \\
                   |    ASSERT_GT(this->${fromPortHistoryName(p.getUnqualifiedName)}->size(), static_cast<U32>(index)) \\
                   |      << "\\n" \\
                   |      << __FILE__ << ":" << __LINE__ << "\\n" \\
                   |      << "  Value:    Index into history of ${p.getUnqualifiedName}\\n" \\
                   |      << "  Expected: Less than size of history (" \\
                   |      << this->${fromPortHistoryName(p.getUnqualifiedName)}->size() << ")\\n" \\
                   |      << "  Actual:   " << index << "\\n"; \\
                   |      const ${fromPortEntryName(p.getUnqualifiedName)}& _e = \\
                   |        this->${fromPortHistoryName(p.getUnqualifiedName)}->at(index); \\
                   |"""
              ) ++ params.flatMap((name, _) =>
                lines(
                  s"""    ASSERT_EQ(_$name, _e.$name) \\
                     |      << "\\n" \\
                     |      << __FILE__ << ":" << __LINE__ << "\\n" \\
                     |      << "  Value:    Value of argument $name at index " \\
                     |      << index \\
                     |      << " in history of ${p.getUnqualifiedName}\\n" \\
                     |      << "  Expected: " << _$name << "\\n" \\
                     |      << "  Actual:   " << _e.$name << "\\n"; \\
                     |"""
                )
              )
            },
            lines("  }")
          )
        })
      ),
    )
  }

  private def getCmdMacros = {
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Macros for command history assertions"),
        Line.blank :: lines(
          """#define ASSERT_CMD_RESPONSE_SIZE(size) \
            |  this->assertCmdResponse_size(__FILE__, __LINE__, size)
            |
            |#define ASSERT_CMD_RESPONSE(index, opCode, cmdSeq, response) \
            |  this->assertCmdResponse(__FILE__, __LINE__, index, opCode, cmdSeq, response)
            |"""
        )
      )
    )
  }

  private def getEventMacros = {
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Macros for event history assertions"),
        Line.blank :: lines(
          """#define ASSERT_EVENTS_SIZE(size) \
            |  this->assertEvents_size(__FILE__, __LINE__, size)
            |"""
        ),
        sortedEvents.flatMap((id, event) => {
          val params = eventParamTypeMap(id).map((name, _) => s", _$name").mkString("")

          Line.blank :: lines(
            s"""#define ASSERT_EVENTS_${event.getName}_SIZE(size) \\
               |  this->${eventSizeAssertionFuncName(event.getName)}(__FILE__, __LINE__, size)
               |"""
          ) ++ (eventParamTypeMap(id) match {
            case Nil => Nil
            case _ => Line.blank :: lines(
              s"""#define ASSERT_EVENTS_${event.getName}(size$params) \\
                 |  this->${eventAssertionFuncName(event.getName)}(__FILE__, __LINE__, size$params)
                 |"""
            )
          })
        })
      )
    )
  }

  private def getTlmMacros = {
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Macros for telemetry history assertions"),
        Line.blank :: lines(
          """#define ASSERT_TLM_SIZE(size) \
            |  this->assertTlm_size(__FILE__, __LINE__, size)
            |"""
        ),
        sortedChannels.flatMap((_, channel) =>
          Line.blank :: lines(
            s"""#define ASSERT_TLM_${channel.getName}_SIZE(size) \\
               |  this->${tlmSizeAssertionFuncName(channel.getName)}(__FILE__, __LINE__, size)
               |
               |#define ASSERT_TLM_${channel.getName}(index, value) \\
               |  this->${tlmAssertionFuncName(channel.getName)}(__FILE__, __LINE__, index, value)
               |"""
          )
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
    def writeAssertFuncs(id: Event.Id, event: Event) = {
      val eventsSize = eventParamTypeMap(id) match {
        case Nil => eventSizeName(event.getName)
        case _ => s"${eventHistoryName(event.getName)}->size()"
      }

      functionClassMember(
        Some(s"Event: ${event.getName}"),
        eventSizeAssertionFuncName(event.getName),
        sizeAssertionFunctionParams,
        CppDoc.Type("void"),
        lines(
          s"""ASSERT_EQ(size, this->$eventsSize)
             |  << "\\n"
             |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
             |  << "  Value:    Size of history for event ${event.getName}\\n"
             |  << "  Expected: " << size << "\\n"
             |  << "  Actual:   " << this->$eventsSize << "\\n";
             |"""
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      ) :: (eventParamTypeMap(id) match {
        case Nil => Nil
        case _ => List(
          functionClassMember(
            Some(s"Event: ${event.getName}"),
            eventAssertionFuncName(event.getName),
            assertionFunctionParams ++
              event.aNode._2.data.params.map(aNode => {
                val data = aNode._2.data
                CppDoc.Function.Param(
                  CppDoc.Type(writeCppType(s.a.typeMap(data.typeName.id))),
                  data.name,
                  AnnotationCppWriter.asStringOpt(aNode)
                )
              }),
            CppDoc.Type("void"),
            List.concat(
              lines(
                s"""ASSERT_GT(this->$eventsSize, __index)
                   |  << "\\n"
                   |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
                   |  << "  Value:    Index into history of event ${event.getName}\\n"
                   |  << "  Expected: Less than size of history ("
                   |  << this->$eventsSize << ")\\n"
                   |  << "  Actual:   " << __index << "\\n";
                   |const ${eventEntryName(event.getName)}& _e =
                   |  this->${eventHistoryName(event.getName)}->at(__index);
                   |"""
              ),
              eventParamTypeMap(id).flatMap((name, tn) =>
                lines(
                  s"""ASSERT_EQ($name, ${writeEventValue(s"_e.$name", tn)})
                     |  << "\\n"
                     |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
                     |  << "  Value:    Value of argument $name at index "
                     |  << __index
                     |  << " in history of event ${event.getName}\\n"
                     |  << "  Expected: " << $name << "\\n"
                     |  << "  Actual:   " << ${writeEventValue(s"_e.$name", tn)} << "\\n";
                     |"""
                )
              )
            ),
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        )
      })
    }

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
            lines(
              raw"""ASSERT_EQ(size, this->eventsSize)
                   |  << "\n"
                   |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
                   |  << "  Value:    Total size of all event histories\n"
                   |  << "  Expected: " << size << "\n"
                   |  << "  Actual:   " << this->eventsSize << "\n";
                   |"""
            ),
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        )
        else Nil,
        sortedEvents.flatMap((id, event) => writeAssertFuncs(id, event))
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
            lines(
              raw"""ASSERT_EQ(size, this->tlmSize)
                   |  << "\n"
                   |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
                   |  << "  Value:    Total size of all telemetry histories\n"
                   |  << "  Expected: " << size << "\n"
                   |  << "  Actual:   " << this->tlmSize << "\n";
                   |"""
            ),
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
            lines(
              s"""ASSERT_EQ(this->${tlmHistoryName(channel.getName)}->size(), size)
                 |  << "\\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
                 |  << "  Value:    Size of history for telemetry channel ${channel.getName}\\n"
                 |  << "  Expected: " << size << "\\n"
                 |  << "  Actual:   " << this->${tlmHistoryName(channel.getName)}->size() << "\\n";
                 |"""
            ),
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          ),
          functionClassMember(
            Some(s"Channel: ${channel.getName}"),
            tlmAssertionFuncName(channel.getName),
            assertionFunctionParams ++ List(
              CppDoc.Function.Param(
                CppDoc.Type(writeCppType(channel.channelType)),
                "val",
                Some("The channel value")
              )
            ),
            CppDoc.Type("void"),
            lines(
              s"""ASSERT_LT(__index, this->${tlmHistoryName(channel.getName)}->size())
                 |  << "\\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
                 |  << "  Value:    Index into history of telemetry channel ${channel.getName}\\n"
                 |  << "  Expected: Less than size of history ("
                 |  << this->${tlmHistoryName(channel.getName)}->size() << ")\\n"
                 |  << "  Actual:   " << __index << "\\n";
                 |const ${tlmEntryName(channel.getName)}& _e =
                 |  this->${tlmHistoryName(channel.getName)}->at(__index);
                 |ASSERT_EQ(val, ${writeValue("_e.arg", channel.channelType)})
                 |  << "\\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
                 |  << "  Value:    Value at index "
                 |  << __index
                 |  << " on telemetry channel ${channel.getName}\\n"
                 |  << "  Expected: " << val << "\\n"
                 |  << "  Actual:   " << _e.arg << "\\n";
                 |"""
            ),
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        ))
      )
    )
  }

}
