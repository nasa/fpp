package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component Google Test harness base classes */
case class ComponentGTestBaseWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  private val fileName = ComputeCppFiles.FileNames.getComponentGTestBase(componentName)

  private val name = componentName

  private val namespaceIdentList = componentNamespaceIdentList

  private val relativeFileName = s.getRelativePath(fileName).toString

  private val symbol = componentSymbol

  private val testerFileName = ComputeCppFiles.FileNames.getComponentTesterBase(componentName)

  private val testerRelativeFileName = s.getRelativePath(testerFileName).toString

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
      s"$testerRelativeFileName.hpp",
      "gtest/gtest.h"
    ).sorted.map(CppWriter.headerString).map(line)
    linesMember(
      addBlankPrefix(userHeaders)
    )
  }

  private def getCppIncludes = {
    val userHeader = lines(CppWriter.headerString(s"$relativeFileName.hpp"))
    linesMember(
      addBlankPrefix(userHeader),
      CppDoc.Lines.Cpp
    )
  }

  private def getMacros = {
    List.concat(
      guardedList (hasTypedOutputPorts) (getPortMacros),
      guardedList (hasCommands) (getCmdMacros),
      guardedList (hasEvents) (getEventMacros),
      guardedList (hasTelemetry) (getTlmMacros),
      guardedList (hasDataProducts) (
        List.concat(
          guardedList (hasProductGetPort) (getProductGetMacros),
          guardedList (hasProductRequestPort) (getProductRequestMacros),
          getProductSendMacros
        )
      )
    )
  }

  private def getClassMembers = {
    List.concat(
      getConstructors,
      guardedList (hasTypedOutputPorts) (getPortAssertFunctions),
      guardedList (hasCommands) (getCmdAssertFunctions),
      guardedList (hasEvents) (getEventAssertFunctions),
      guardedList (hasTelemetry) (getTlmAssertFunctions),
      guardedList (hasDataProducts) (
        List.concat(
          guardedList (hasProductGetPort) (getProductGetAssertFunctions),
          guardedList (hasProductRequestPort) (getProductRequestAssertFunctions),
          getProductSendAssertFunctions
        )
      )
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

  private def getPortMacros = List(
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
          val paramList = params.map((name, _, _) => s", _$name").mkString("")
          val portName = p.getUnqualifiedName
          val historyName = fromPortHistoryName(portName)
          val sizeAssertFnName = fromPortSizeAssertionFuncName(portName)
          val entryName = fromPortEntryName(portName)

          List.concat(
            Line.blank :: lines(
              s"""#define ASSERT_from_${portName}_SIZE(size) \\
                 |  this->$sizeAssertFnName(__FILE__, __LINE__, size)
                 |"""
            ),
            addBlankPrefix(
              lines(
                s"""#define ASSERT_from_$portName(index$paramList) \\
                   |  { \\
                   |    ASSERT_GT(this->$historyName->size(), static_cast<U32>(index)) \\
                   |      << "\\n" \\
                   |      << __FILE__ << ":" << __LINE__ << "\\n" \\
                   |      << "  Value:    Index into history of $portName\\n" \\
                   |      << "  Expected: Less than size of history (" \\
                   |      << this->$historyName->size() << ")\\n" \\
                   |      << "  Actual:   " << index << "\\n"; \\
                   |      const $entryName& _e = \\
                   |        this->$historyName->at(index); \\
                   |"""
              ) ++ params.flatMap((name, _, _) =>
                lines(
                  s"""    ASSERT_EQ(_$name, _e.$name) \\
                     |      << "\\n" \\
                     |      << __FILE__ << ":" << __LINE__ << "\\n" \\
                     |      << "  Value:    Value of argument $name at index " \\
                     |      << index \\
                     |      << " in history of $portName\\n" \\
                     |      << "  Expected: " << _$name << "\\n" \\
                     |      << "  Actual:   " << _e.$name << "\\n"; \\
                     |"""
                )
              )
            ),
            lines("  }")
          )
        })
      ),
    )
  )

  private def getCmdMacros: List[CppDoc.Member] = List(
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
  )

  private def getEventMacros = List(
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Macros for event history assertions"),
        Line.blank :: lines(
          """#define ASSERT_EVENTS_SIZE(size) \
            |  this->assertEvents_size(__FILE__, __LINE__, size)
            |"""
        ),
        sortedEvents.flatMap((id, event) => {
          val params = eventParamTypeMap(id).map((name, _, _) => s", _$name").mkString("")
          val eventName = event.getName
          val sizeAssertFn = eventSizeAssertionFuncName(eventName)
          val eventAssertFn = eventAssertionFuncName(eventName)

          Line.blank :: lines(
            s"""#define ASSERT_EVENTS_${eventName}_SIZE(size) \\
               |  this->$sizeAssertFn(__FILE__, __LINE__, size)
               |"""
          ) ++ (eventParamTypeMap(id) match {
            case Nil => Nil
            case _ => Line.blank :: lines(
              s"""#define ASSERT_EVENTS_$eventName(size$params) \\
                 |  this->$eventAssertFn(__FILE__, __LINE__, size$params)
                 |"""
            )
          })
        })
      )
    )
  )

  private def getTlmMacros = List(
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Macros for telemetry history assertions"),
        Line.blank :: lines(
          """#define ASSERT_TLM_SIZE(size) \
            |  this->assertTlm_size(__FILE__, __LINE__, size)
            |"""
        ),
        sortedChannels.flatMap((_, channel) => {
          val channelName = channel.getName
          val sizeAssertFunc = tlmSizeAssertionFuncName(channelName)
          val assertFunc = tlmAssertionFuncName(channelName)
          Line.blank :: lines(
            s"""#define ASSERT_TLM_${channelName}_SIZE(size) \\
               |  this->$sizeAssertFunc(__FILE__, __LINE__, size)
               |
               |#define ASSERT_TLM_$channelName(index, value) \\
               |  this->$assertFunc(__FILE__, __LINE__, index, value)
               |"""
          )
        })
      )
    )
  )

  private def getProductGetMacros: List[CppDoc.Member] = List(
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Macros for product get assertions"),
        Line.blank :: lines(
          """#define ASSERT_PRODUCT_GET_SIZE(size) \
            |  this->assertProductGet_size(__FILE__, __LINE__, size)
            |
            |#define ASSERT_PRODUCT_GET(index, id, size) \
            |  this->assertProductGet(__FILE__, __LINE__, index, id, size)
            |"""
        )
      )
    )
  )

  private def getProductRequestMacros: List[CppDoc.Member] = List(
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Macros for product request assertions"),
        Line.blank :: lines(
          """#define ASSERT_PRODUCT_REQUEST_SIZE(size) \
            |  this->assertProductRequest_size(__FILE__, __LINE__, size)
            |
            |#define ASSERT_PRODUCT_REQUEST(index, id, size) \
            |  this->assertProductRequest(__FILE__, __LINE__, index, id, size)
            |"""
        )
      )
    )
  )

  private def getProductSendMacros: List[CppDoc.Member] = List(
    linesMember(
      List.concat(
        CppDocWriter.writeBannerComment("Macros for product send assertions"),
        Line.blank :: lines(
          """#define ASSERT_PRODUCT_SEND_SIZE(size) \
            |  this->assertProductSend_size(__FILE__, __LINE__, size)
            |
            |#define ASSERT_PRODUCT_SEND(index, id, priority, timeTag, procTypes, userData, dpState, dataSize, buffer) \
            |    assertProductSend(__FILE__, __LINE__, index, id, priority, timeTag, procTypes, userData, dpState, dataSize, buffer)
            |"""
        )
      )
    )
  )

  private def getPortAssertFunctions = {
    addAccessTagAndComment(
      "protected",
      "From ports",
      {
        val fromPortHistorySize = functionClassMember(
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
        val fromPorts = typedOutputPorts.map(p => {
          val portName = p.getUnqualifiedName
          val portHistory = fromPortHistoryName(portName)
          val portSize = portParamTypeMap(portName) match {
            case Nil => fromPortHistorySizeName(portName)
            case _ => s"$portHistory->size()"
          }

          functionClassMember(
            Some(s"From port: $portName"),
            fromPortSizeAssertionFuncName(portName),
            sizeAssertionFunctionParams,
            CppDoc.Type("void"),
            lines(
              s"""ASSERT_EQ(size, this->$portSize)
                 |  << "\\n"
                 |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
                 |  << "  Value:    Size of history for $portName\\n"
                 |  << "  Expected: " << size << "\\n"
                 |  << "  Actual:   " << this->$portSize << "\\n";
                 |"""
            ),
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        })
        List.concat(
          guardedList (hasTypedOutputPorts) (List(fromPortHistorySize)),
          fromPorts
        )
      }
    )
  }

  private def getCmdAssertFunctions = {
    addAccessTagAndComment(
      "protected",
      "Commands",
      {
        lazy val historySize =
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
          )
        lazy val historyIndex =
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
        guardedList (hasCommands) (List(historySize, historyIndex))
      }
    )
  }

  private def getEventAssertFunctions = {
    def writeAssertFuncs(id: Event.Id, event: Event) = {
      val eventName = event.getName
      val eventsSize = eventParamTypeMap(id) match {
        case Nil => eventSizeName(eventName)
        case _ => s"${eventHistoryName(eventName)}->size()"
      }

      functionClassMember(
        Some(s"Event: $eventName"),
        eventSizeAssertionFuncName(eventName),
        sizeAssertionFunctionParams,
        CppDoc.Type("void"),
        lines(
          s"""ASSERT_EQ(size, this->$eventsSize)
             |  << "\\n"
             |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
             |  << "  Value:    Size of history for event $eventName\\n"
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
            Some(s"Event: $eventName"),
            eventAssertionFuncName(eventName),
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
            {
              val entryName = eventEntryName(eventName)
              val historyName = eventHistoryName(eventName)
              List.concat(
                lines(
                  s"""ASSERT_GT(this->$eventsSize, __index)
                     |  << "\\n"
                     |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
                     |  << "  Value:    Index into history of event $eventName\\n"
                     |  << "  Expected: Less than size of history ("
                     |  << this->$eventsSize << ")\\n"
                     |  << "  Actual:   " << __index << "\\n";
                     |const $entryName& _e =
                     |  this->$historyName->at(__index);
                     |"""
                ),
                eventParamTypeMap(id).flatMap((name, tn, _) => {
                  val assertEq = writeEventAssertEq(tn)
                  val eventValue = writeEventValue(s"_e.$name", tn)
                  lines(
                    s"""$assertEq($name, $eventValue)
                       |  << "\\n"
                       |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
                       |  << "  Value:    Value of argument $name at index "
                       |  << __index
                       |  << " in history of event $eventName\\n"
                       |  << "  Expected: " << $name << "\\n"
                       |  << "  Actual:   " << $eventValue << "\\n";
                       |"""
                  )
                })
              )
            },
            CppDoc.Function.NonSV,
            CppDoc.Function.Const
          )
        )
      })
    }

    addAccessTagAndComment(
      "protected",
      "Events",
      {
        lazy val historySize = functionClassMember(
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
        List.concat(
          guardedList (hasEvents) (List(historySize)),
          sortedEvents.flatMap((id, event) => writeAssertFuncs(id, event))
        )
      }
    )
  }

  private def getTlmAssertFunctions = addAccessTagAndComment(
    "protected",
    "Telemetry",
    {
      val historySize = functionClassMember(
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
      def channelHistorySize(channel: TlmChannel) = {
        val channelName = channel.getName
        val historyName = tlmHistoryName(channelName)
        functionClassMember(
          Some(s"Channel: $channelName"),
          tlmSizeAssertionFuncName(channelName),
          sizeAssertionFunctionParams,
          CppDoc.Type("void"),
          lines(
            s"""ASSERT_EQ(this->$historyName->size(), size)
               |  << "\\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
               |  << "  Value:    Size of history for telemetry channel $channelName\\n"
               |  << "  Expected: " << size << "\\n"
               |  << "  Actual:   " << this->$historyName->size() << "\\n";
               |"""
          ),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      }
      def channelIndex(channel: TlmChannel) = {
        val channelName = channel.getName
        val historyName = tlmHistoryName(channelName)
        val entryName = tlmEntryName(channelName)
        val assertEq = writeAssertEq(channel.channelType)
        val value = writeValue("_e.arg", channel.channelType)
        functionClassMember(
          Some(s"Channel: $channelName"),
          tlmAssertionFuncName(channelName),
          assertionFunctionParams ++ List(
            CppDoc.Function.Param(
              CppDoc.Type(writeCppType(channel.channelType)),
              "val",
              Some("The channel value")
            )
          ),
          CppDoc.Type("void"),
          lines(
            s"""ASSERT_LT(__index, this->$historyName->size())
               |  << "\\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
               |  << "  Value:    Index into history of telemetry channel $channelName\\n"
               |  << "  Expected: Less than size of history ("
               |  << this->$historyName->size() << ")\\n"
               |  << "  Actual:   " << __index << "\\n";
               |const $entryName& _e =
               |  this->$historyName->at(__index);
               |$assertEq(val, $value)
               |  << "\\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\\n"
               |  << "  Value:    Value at index "
               |  << __index
               |  << " on telemetry channel $channelName\\n"
               |  << "  Expected: " << val << "\\n"
               |  << "  Actual:   " << _e.arg << "\\n";
               |"""
          ),
          CppDoc.Function.NonSV,
          CppDoc.Function.Const
        )
      }

      List.concat(
        guardedList (hasChannels) (List(historySize)),
        sortedChannels.flatMap((_, channel) => List(
          channelHistorySize(channel),
          channelIndex(channel)
        ))
      )
    }
  )

  private def getProductGetAssertFunctions = {
    lazy val historySize =
      functionClassMember(
        Some("Assert size of product get history"),
        "assertProductGet_size",
        sizeAssertionFunctionParams,
        CppDoc.Type("void"),
        lines(
          raw"""ASSERT_EQ(size, this->productGetHistory->size())
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Size of product get history\n"
               |  << "  Expected: " << size << "\n"
               |  << "  Actual:   " << this->productGetHistory->size() << "\n";
               |"""
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    lazy val historyIndex =
      functionClassMember(
        Some("Assert the product get history at index"),
        "assertProductGet",
        assertionFunctionParams ++ List(
          CppDoc.Function.Param(
            CppDoc.Type("FwDpIdType"),
            "id",
            Some("The container ID")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("FwSizeType"),
            "size",
            Some("The size of the requested buffer")
          )
        ),
        CppDoc.Type("void"),
        lines(
          raw"""ASSERT_LT(__index, this->productGetHistory->size())
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Index into product get history\n"
               |  << "  Expected: Less than size of product get history ("
               |  << this->productGetHistory->size() << ")\n"
               |  << "  Actual:   " << __index << "\n";
               |const DpGet& e = this->productGetHistory->at(__index);
               |ASSERT_EQ(id, e.id)
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Id at index "
               |  << __index
               |  << " in product get history\n"
               |  << "  Expected: " << id << "\n"
               |  << "  Actual:   " << e.id << "\n";
               |ASSERT_EQ(size, e.size)
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Size at index "
               |  << __index
               |  << " in product get history\n"
               |  << "  Expected: " << size << "\n"
               |  << "  Actual:   " << e.size << "\n";
               |"""
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    addAccessTagAndComment(
      "protected",
      "Data Product Get",
      List(historySize, historyIndex)
    )
  }

  private def getProductRequestAssertFunctions = {
    lazy val historySize =
      functionClassMember(
        Some("Assert size of product request history"),
        "assertProductRequest_size",
        sizeAssertionFunctionParams,
        CppDoc.Type("void"),
        lines(
          raw"""ASSERT_EQ(size, this->productRequestHistory->size())
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Size of product request history\n"
               |  << "  Expected: " << size << "\n"
               |  << "  Actual:   " << this->productRequestHistory->size() << "\n";
               |"""
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    lazy val historyIndex =
      functionClassMember(
        Some("Assert the product request history at index"),
        "assertProductRequest",
        assertionFunctionParams ++ List(
          CppDoc.Function.Param(
            CppDoc.Type("FwDpIdType"),
            "id",
            Some("The container ID")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("FwSizeType"),
            "size",
            Some("The size of the requested buffer")
          )
        ),
        CppDoc.Type("void"),
        lines(
          raw"""ASSERT_LT(__index, this->productRequestHistory->size())
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Index into product request history\n"
               |  << "  Expected: Less than size of product request history ("
               |  << this->productRequestHistory->size() << ")\n"
               |  << "  Actual:   " << __index << "\n";
               |const DpRequest& e = this->productRequestHistory->at(__index);
               |ASSERT_EQ(id, e.id)
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Id at index "
               |  << __index
               |  << " in product request history\n"
               |  << "  Expected: " << id << "\n"
               |  << "  Actual:   " << e.id << "\n";
               |ASSERT_EQ(size, e.size)
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Size at index "
               |  << __index
               |  << " in product request history\n"
               |  << "  Expected: " << size << "\n"
               |  << "  Actual:   " << e.size << "\n";
               |"""
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    addAccessTagAndComment(
      "protected",
      "Data Product Request",
      List(historySize, historyIndex)
    )
  }

  private def getProductSendAssertFunctions = {
    lazy val historySize =
      functionClassMember(
        Some("Assert size of product send history"),
        "assertProductSend_size",
        sizeAssertionFunctionParams,
        CppDoc.Type("void"),
        lines(
          raw"""ASSERT_EQ(size, this->productSendHistory->size())
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Size of product send history\n"
               |  << "  Expected: " << size << "\n"
               |  << "  Actual:   " << this->productSendHistory->size() << "\n";
               |"""
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    lazy val historyIndex =
      functionClassMember(
        Some(
          """|Assert the product send history at index
             |
             |This function sets the output buffer, deserializes and checks the
             |data product header, and sets the deserialization pointer to the start
             |of the data payload. User-written code can then check the data payload.
             |""".stripMargin
        ),
        "assertProductSend",
        assertionFunctionParams ++ List(
          CppDoc.Function.Param(
            CppDoc.Type("FwDpIdType"),
            "id",
            Some("The expected container ID (input)")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("FwDpPriorityType"),
            "priority",
            Some("The expected priority (input)")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("const Fw::Time&"),
            "timeTag",
            Some("The expected time tag (input)")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("Fw::DpCfg::ProcType::SerialType"),
            "procTypes",
            Some("The expected processing types (input)")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("const Fw::DpContainer::Header::UserData&"),
            "userData",
            Some("The expected user data (input)")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("Fw::DpState"),
            "dpState",
            Some("The expected data product state (input)")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("FwSizeType"),
            "dataSize",
            Some("The expected data size (input)")
          ),
          CppDoc.Function.Param(
            CppDoc.Type("Fw::Buffer&"),
            "historyBuffer",
            Some("The buffer from the history (output)")
          )
        ),
        CppDoc.Type("void"),
        lines(
          raw"""ASSERT_LT(__index, this->productSendHistory->size())
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Index into product send history\n"
               |  << "  Expected: Less than size of product send history ("
               |  << this->productSendHistory->size() << ")\n"
               |  << "  Actual:   " << __index << "\n";
               |const DpSend& e = this->productSendHistory->at(__index);
               |// Set the history buffer output
               |historyBuffer = e.buffer;
               |// Check the container id
               |ASSERT_EQ(e.id, id)
               |  << "\n"
               |  << __callSiteFileName << ":" << __callSiteLineNumber << "\n"
               |  << "  Value:    Container ID at index " << __index << " in product send history\n"
               |  << "  Expected: " << id << "\n"
               |  << "  Actual:   " << e.id << "\n";
               |// Check the header
               |Fw::TestUtil::DpContainerHeader header;
               |header.deserialize(__callSiteFileName, __callSiteLineNumber, historyBuffer);
               |header.check(
               |    __callSiteFileName,
               |    __callSiteLineNumber,
               |    historyBuffer,
               |    id,
               |    priority,
               |    timeTag,
               |    procTypes,
               |    userData,
               |    dpState,
               |    dataSize
               |);
               |"""
        ),
        CppDoc.Function.NonSV,
        CppDoc.Function.Const
      )
    addAccessTagAndComment(
      "protected",
      "Data Product Send",
      List(historySize, historyIndex)
    )
  }

}
