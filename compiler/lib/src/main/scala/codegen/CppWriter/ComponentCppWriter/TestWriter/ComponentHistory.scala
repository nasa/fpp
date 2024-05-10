package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for component test harness history class */
case class ComponentHistory(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  def getClassMember: List[CppDoc.Class.Member] = {
    lazy val history = linesClassMember(
      lines(
        """|
           |//! \class History
           |//! \brief A history of port inputs
           |//!
           |template<typename T>
           |class History
           |{
           |
           |  public:
           |
           |    //! Create a History
           |    History(
           |       const U32 maxSize //!< The maximum history size
           |    ) :
           |      numEntries(0),
           |      maxSize(maxSize)
           |    {
           |      this->entries = new T[maxSize];
           |    }
           |
           |    //! Destroy a History
           |    ~History()
           |    {
           |      delete[] this->entries;
           |    }
           |
           |    //! Clear the history
           |    //!
           |    void clear()
           |    {
           |      this->numEntries = 0;
           |    }
           |
           |    //! Push an item onto the history
           |    //!
           |    void push_back(
           |        const T& entry //!< The item
           |    )
           |    {
           |      FW_ASSERT(this->numEntries < this->maxSize);
           |      entries[this->numEntries++] = entry;
           |    }
           |
           |    //! Get an item at an index
           |    //!
           |    //! \return The item at index i
           |    const T& at(
           |        const U32 i //!< The index
           |    ) const
           |    {
           |      FW_ASSERT(i < this->numEntries);
           |      return entries[i];
           |    }
           |
           |    //! Get the number of entries in the history
           |    //!
           |    //! \return The number of entries in the history
           |    U32 size() const
           |    {
           |      return this->numEntries;
           |    }
           |
           |  private:
           |
           |    //! The number of entries in the history
           |    U32 numEntries;
           |
           |    //! The maximum history size
           |    const U32 maxSize;
           |
           |    //! The entries
           |    T* entries;
           |
           |};
           |"""
      ),
      CppDoc.Lines.Hpp
    )
    addAccessTagAndComment(
      "protected",
      "History class",
      guardedList (hasHistories) (List(history)),
      CppDoc.Lines.Hpp
    )
  }

  def getTypeMembers: List[CppDoc.Class.Member] = addAccessTagAndComment(
    "protected",
    "History types",
    List.concat(
      guardedList (hasTypedOutputPorts) (getPortHistoryTypes),
      guardedList (hasCommands) (getCmdHistoryTypes),
      guardedList (hasEvents) (getEventHistoryTypes),
      guardedList (hasTelemetry) (getTlmHistoryTypes),
      guardedList (hasDataProducts) (getDpHistoryTypes),
    ),
    CppDoc.Lines.Hpp
  )

  def getFunctionMembers: List[CppDoc.Class.Member] = List.concat(
    guardedList (hasHistories) (getClearHistoryFunction),
    guardedList (hasTypedOutputPorts) (getPortHistoryFunctions),
    guardedList (hasEvents) (getEventHistoryFunctions),
    guardedList (hasTelemetry) (getTlmHistoryFunctions),
  )

  def getVariableMembers: List[CppDoc.Class.Member] = addAccessTagAndComment(
    "protected",
    "History member variables",
    List.concat(
      guardedList (hasTypedOutputPorts) (getPortHistoryVariables),
      guardedList (hasCommands) (getCmdHistoryVariables),
      guardedList (hasEvents) (getEventHistoryVariables),
      guardedList (hasTelemetry) (getTlmHistoryVariables),
      guardedList (hasDataProducts) (getDpHistoryVariables),
    ),
    CppDoc.Lines.Hpp
  )

  private def getClearHistoryFunction: List[CppDoc.Class.Member] = {
    lazy val clearHistory = functionClassMember(
      Some("Clear all history"),
      "clearHistory",
      Nil,
      CppDoc.Type("void"),
      List.concat(
        guardedList (hasTypedOutputPorts) (lines("this->clearFromPortHistory();")),
        guardedList (hasCommands) (lines("this->cmdResponseHistory->clear();")),
        guardedList (hasEvents) (
          lines(
            """|#if FW_ENABLE_TEXT_LOGGING
               |this->textLogHistory->clear();
               |#endif
               |this->clearEvents();
               |"""
          )
        ),
        guardedList (hasChannels) (lines("this->clearTlm();")),
        guardedList (hasDataProducts) (
          List.concat(
            guardedList (hasProductGetPort) (lines("this->productGetHistory->clear();")),
            guardedList (hasProductRequestPort) (lines("this->productRequestHistory->clear();")),
            lines("this->productSendHistory->clear();")
          )
        )
      )
    )
    addAccessTagAndComment(
      "protected",
      "History functions",
      List(clearHistory)
    )
  }

  private def getPortHistoryTypes: List[CppDoc.Class.Member] = {
    List(
      linesClassMember(
        typedOutputPorts.flatMap(p => {
          val portName = testerPortName(p)
          val entryName = fromPortEntryName(p.getUnqualifiedName)
          val params = portParamTypeMap(p.getUnqualifiedName)
          guardedList (params.size > 0) {
            val constructor = wrapInScope(
              s"$entryName() :",
              lines(
                params.map((n, tn, t) => {
                  t match {
                    case ts: Type.String =>
                      val bufferName = getBufferName(n)
                      s"$n($bufferName, sizeof $bufferName)"
                    case _ => s"$n()"
                  }
                }).mkString(",\n")
              ),
              "{}"
            )
            val members = params.flatMap((n, tn, t) => lines(writeMemberDecl(s, tn, n, t)))
            Line.blank ::
            line(s"//! A history entry for port $portName") ::
            wrapInScope(
              s"struct $entryName {",
              constructor ++ members,
              "};"
            )
          }
        })
      )
    )
  }

  private def getPortHistoryFunctions: List[CppDoc.Class.Member] = {
    val clearHistory = functionClassMember(
      Some("Clear from port history"),
      "clearFromPortHistory",
      Nil,
      CppDoc.Type("void"),
      List.concat(
        lines("this->fromPortHistorySize = 0;"),
        typedOutputPorts.map(p => {
          val portName = p.getUnqualifiedName
          val historySizeName = fromPortHistorySizeName(portName)
          val historyName = fromPortHistoryName(portName)
          portParamTypeMap(p.getUnqualifiedName) match {
            case Nil => line(s"this->$historySizeName = 0;")
            case _ => line(s"this->$historyName->clear();")
          }
        })
      )
    )

    def pushHistoryEntry(p: PortInstance) = {
      val portName = p.getUnqualifiedName
      val entryName = fromPortEntryName(portName)
      val historyName = fromPortHistoryName(portName)
      List.concat(
        lines(s"$entryName _e;"),
        getPortParams(p).map((n, _, _) => line(s"_e.$n = $n;")),
        lines(s"this->$historyName->push_back(_e);")
      )
    }

    def pushEntryAndUpdateSize(p: PortInstance) = {
      val portName = p.getUnqualifiedName
      val inputName = inputPortName(portName)
      val historySizeName = fromPortHistorySizeName(portName)
      functionClassMember(
        Some(s"Push an entry on the history for $inputName"),
        fromPortPushEntryName(p.getUnqualifiedName),
        getPortFunctionParams(p),
        CppDoc.Type("void"),
        List.concat(
          portParamTypeMap(portName) match {
            case Nil => lines(s"this->$historySizeName++;")
            case _ => pushHistoryEntry(p) 
          },
          lines("this->fromPortHistorySize++;")
        )
      )
    }

    clearHistory :: typedOutputPorts.map(pushEntryAndUpdateSize)
  }

  private def getPortHistoryVariables: List[CppDoc.Class.Member] = {
    val portHistorySize = lines(
      s"""|//! The total number of port entries
          |U32 fromPortHistorySize;
          |"""
    )
    val histories = typedOutputPorts.map(
      p => {
        val portName = p.getUnqualifiedName
        val inputName = inputPortName(portName)
        val historySizeName = fromPortHistorySizeName(portName)
        val entryName = fromPortEntryName(portName)
        val historyName = fromPortHistoryName(portName)
        getPortParams(p) match {
          case Nil => lines(
            s"""|//! The size of history for $inputName
                |U32 $historySizeName;
                |""".stripMargin
          )
          case _ => lines(
            s"""|//! The history for $inputName
                |History<$entryName>* $historyName;
                |"""
          )
        }
      }
    )
    val variables = linesClassMember(
      Line.blank ::
      intersperseBlankLines(portHistorySize :: histories),
      CppDoc.Lines.Hpp
    )
    List(variables)
  }

  private def getCmdHistoryTypes: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        Line.blank ::
        line("//! A type representing a command response") ::
          wrapInScope(
            "struct CmdResponse {",
            lines(
              """|FwOpcodeType opCode;
                 |U32 cmdSeq;
                 |Fw::CmdResponse response;
                 |"""
            ),
            "};"
          ),
        CppDoc.Lines.Hpp
      )
    )

  private def getCmdHistoryVariables: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        Line.blank :: lines(
          """|//! The command response history
             |History<CmdResponse>* cmdResponseHistory;
             |"""
        ),
        CppDoc.Lines.Hpp
      )
    )

  private def getDpHistoryTypes: List[CppDoc.Class.Member] = {
    val productGet = guardedList(hasProductGetPort) (
      Line.blank ::
      line("//! A type representing a data product get") ::
      wrapInScope(
        "struct DpGet {",
        lines(
          """|FwDpIdType id;
             |FwSizeType size;
             |"""
        ),
        "};"
      )
    )
    val productRequest = guardedList(hasProductRequestPort) (
      Line.blank ::
      line("//! A type representing a data product request") ::
      wrapInScope(
        "struct DpRequest {",
        lines(
          """|FwDpIdType id;
             |FwSizeType size;
             |"""
        ),
        "};"
      )
    )
    val productSend =
      Line.blank ::
      line("// A type representing a data product send") ::
      wrapInScope(
        "struct DpSend {",
        lines(
          """|FwDpIdType id;
             |Fw::Buffer buffer;
             |"""
        ),
        "};"
      )
    List(
      linesClassMember(
        List.concat(productGet, productRequest, productSend),
        CppDoc.Lines.Hpp
      )
    )
  }

  private def getDpHistoryVariables: List[CppDoc.Class.Member] =
    List(
      linesClassMember(
        List.concat(
          guardedList (hasProductGetPort) (
            Line.blank ::
            lines(
              """|//! The data product get history
                 |History<DpGet>* productGetHistory;"""
            )
          ),
          guardedList (hasProductRequestPort) (
            Line.blank ::
            lines(
              """|//! The data product request history
                 |History<DpRequest>* productRequestHistory;"""
            )
          ),
          Line.blank ::
          lines(
             """|//! The data product send history
                |History<DpSend>* productSendHistory;
                |"""
          )
        ),
        CppDoc.Lines.Hpp
      )
    )

  private def getEventHistoryTypes: List[CppDoc.Class.Member] = {
    val textLogHistory = wrapClassMemberInTextLogGuard(
        linesClassMember(
          Line.blank ::
          line("//! A history entry for text log events") ::
          wrapInScope(
            "struct TextLogEntry {",
            lines(
              """|U32 id;
                 |Fw::Time timeTag;
                 |Fw::LogSeverity severity;
                 |Fw::TextLogString text;
                 |"""
            ),
            "};"
          ),
          CppDoc.Lines.Hpp
        ),
        CppDoc.Lines.Hpp
      )
    val eventHistories = linesClassMember(
      sortedEvents.flatMap((id, event) =>
        getEventParamTypes(event, "Fw::LogStringArg") match {
          case Nil => Nil
          case params =>
            val eventName = event.getName
            val entryName = eventEntryName(eventName)
            Line.blank ::
            line(s"//! A history entry for event $eventName") ::
            wrapInScope(
              s"struct $entryName {",
              params.map((name, tn, _) => line(s"$tn $name;")),
              "};"
            )
        }
      ),
      CppDoc.Lines.Hpp
    )
    List.concat(
      textLogHistory,
      List(eventHistories)
    )
  }

  private def getEventHistoryFunctions: List[CppDoc.Class.Member] = {
    lazy val clearHistory = functionClassMember(
      Some("Clear event history"),
      "clearEvents",
      Nil,
      CppDoc.Type("void"),
      List.concat(
        lines("this->eventsSize = 0;"),
        sortedEvents.map((id, event) => {
          val eventName = event.getName
          val sizeName = eventSizeName(eventName)
          val historyName = eventHistoryName(eventName)
          eventParamTypeMap(id) match {
            case Nil => line(s"this->$sizeName = 0;")
            case _ => line(s"this->$historyName->clear();")
          }
        })
      )
    )
    lazy val printEntry = functionClassMember(
      Some("Print a text log history entry"),
      "printTextLogHistoryEntry",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("const TextLogEntry&"),
          "e"
        ),
        CppDoc.Function.Param(
          CppDoc.Type("FILE*"),
          "file"
        )
      ),
      CppDoc.Type("void"),
      lines(
        """|const char* severityString = "UNKNOWN";
           |
           |switch (e.severity.e) {
           |  case Fw::LogSeverity::FATAL:
           |    severityString = "FATAL";
           |    break;
           |  case Fw::LogSeverity::WARNING_HI:
           |    severityString = "WARNING_HI";
           |    break;
           |  case Fw::LogSeverity::WARNING_LO:
           |    severityString = "WARNING_LO";
           |    break;
           |  case Fw::LogSeverity::COMMAND:
           |    severityString = "COMMAND";
           |    break;
           |  case Fw::LogSeverity::ACTIVITY_HI:
           |    severityString = "ACTIVITY_HI";
           |    break;
           |  case Fw::LogSeverity::ACTIVITY_LO:
           |    severityString = "ACTIVITY_LO";
           |    break;
           |  case Fw::LogSeverity::DIAGNOSTIC:
           |   severityString = "DIAGNOSTIC";
           |    break;
           |  default:
           |    severityString = "SEVERITY ERROR";
           |    break;
           |}
           |
           |fprintf(
           |  file,
           |  "EVENT: (%" PRI_FwEventIdType ") (%" PRI_FwTimeBaseStoreType ":%" PRIu32 ",%" PRIu32 ") %s: %s\n",
           |  e.id,
           |  static_cast<FwTimeBaseStoreType>(e.timeTag.getTimeBase()),
           |  e.timeTag.getSeconds(),
           |  e.timeTag.getUSeconds(),
           |  severityString,
           |  e.text.toChar()
           |);
           |"""
      ),
      CppDoc.Function.Static
    )
    lazy val printHistory = functionClassMember(
      Some("Print the text log history"),
      "printTextLogHistory",
      List(
        CppDoc.Function.Param(
          CppDoc.Type("FILE* const"),
          "file"
        )
      ),
      CppDoc.Type("void"),
      lines(
        """|for (U32 i = 0; i < this->textLogHistory->size(); i++) {
           |  this->printTextLogHistoryEntry(
           |    this->textLogHistory->at(i),
           |    file
           |  );
           |}
           |"""
      )
    )
    List.concat(
      List(clearHistory),
      wrapClassMembersInTextLogGuard(List(printEntry, printHistory))
    )
  }

  private def getEventHistoryVariables: List[CppDoc.Class.Member] = {
    val eventsSize = linesClassMember(
      Line.blank :: lines(
        """|//! The total number of events seen
           |U32 eventsSize;
           |"""
      )
    )
    val textLogHistory = wrapClassMemberInTextLogGuard(
      linesClassMember(
        Line.blank :: lines(
          """|//! The history of text log events
             |History<TextLogEntry>* textLogHistory;
             |"""
        ),
        CppDoc.Lines.Hpp
      ),
      CppDoc.Lines.Hpp
    )
    val eventHistories = linesClassMember(
      sortedEvents.flatMap((id, event) => {
        val eventName = event.getName
        val sizeName = eventSizeName(eventName)
        val entryName = eventEntryName(eventName)
        val historyName = eventHistoryName(eventName)
        eventParamTypeMap(id) match {
          case Nil => Line.blank :: lines(
            s"""|//! Size of history for event $eventName
                |U32 $sizeName;
                |"""
          )
          case _ => Line.blank :: lines(
            s"""|//! The history of $eventName events
                |History<$entryName>* $historyName;
                |"""
          )
        }
      }),
      CppDoc.Lines.Hpp
    )
    List.concat(
      List(eventsSize),
      textLogHistory,
      List(eventHistories)
    )
  }

  private def getTlmHistoryTypes: List[CppDoc.Class.Member] = List(
    linesClassMember(
      sortedChannels.flatMap((_, channel) => {
        val channelName = channel.getName
        val entryName = tlmEntryName(channelName)
        val channelType = writeChannelType(channel.channelType, "Fw::TlmString")
        Line.blank ::
        line(s"//! A history entry for telemetry channel $channelName") ::
        wrapInScope(
          s"struct $entryName {",
          lines(
            s"""|Fw::Time timeTag;
                |$channelType arg;
                |"""
          ),
          "};"
        )
      }),
      CppDoc.Lines.Hpp
    )
  )

  private def getTlmHistoryFunctions: List[CppDoc.Class.Member] = List(
    functionClassMember(
      Some("Clear telemetry history"),
      "clearTlm",
      Nil,
      CppDoc.Type("void"),
      List.concat(
        lines("this->tlmSize = 0;"),
        sortedChannels.map((_, channel) =>
          line(s"this->${tlmHistoryName(channel.getName)}->clear();")
        )
      )
    )
  )

  private def getTlmHistoryVariables: List[CppDoc.Class.Member] = List(
    linesClassMember(
      List.concat(
        Line.blank :: lines(
          """|//! The total number of telemetry inputs seen
             |U32 tlmSize;
             |"""
        ),
        sortedChannels.flatMap((_, channel) => {
          val channelName = channel.getName
          val entryName = tlmEntryName(channelName)
          val historyName = tlmHistoryName(channelName)
          Line.blank :: lines(
            s"""|//! The history of $channelName values
                |History<$entryName>* $historyName;
                |"""
          )
        })
      ),
      CppDoc.Lines.Hpp
    )
  )

}
