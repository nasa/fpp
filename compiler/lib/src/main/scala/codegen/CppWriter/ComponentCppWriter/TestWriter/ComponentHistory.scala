package fpp.compiler.codegen

import fpp.compiler.analysis.*
import fpp.compiler.ast.*
import fpp.compiler.util.*

/** Writes out C++ for component test harness history class */
case class ComponentHistory(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  def getMembers: List[CppDoc.Class.Member] = {
    List.concat(
      getClearHistoryFunction,
    )
  }

  def getHistoryClass: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "History class",
      List(
        linesClassMember(
          lines(
            """|//! \class History
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
               |        T entry //!< The item
               |    )
               |    {
               |      FW_ASSERT(this->numEntries < this->maxSize);
               |      entries[this->numEntries++] = entry;
               |    }
               |
               |    //! Get an item at an index
               |    //!
               |    //! \return The item at index i
               |    T at(
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
      )
    )
  }

  private def getClearHistoryFunction: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "History functions",
      List(
        functionClassMember(
          Some("Clear all history"),
          "clearHistory",
          Nil,
          CppDoc.Type("void"),
          List.concat(
            if typedOutputPorts.nonEmpty then lines("this->clearFromPortHistory();")
            else Nil,
            if hasCommands || hasParameters then lines("this->cmdResponseHistory->clear();")
            else Nil,
            if hasEvents then lines(
              """|this->textLogHistory->clear();
                 |this->clearEvents();
                 |"""
            )
            else Nil,
            if hasChannels then lines("this->clearTlm();")
            else Nil,
          )
        )
      )
    )
  }

  private def getPortHistoryTypes: List[CppDoc.Class.Member] = {
    List(
      linesClassMember(
        typedOutputPorts.flatMap(p =>
          wrapInScope(
            s"struct ${fromPortEntryName(p.getUnqualifiedName)}",
            lines(getPortParams(p).map((name, tn) => s"$tn $name").mkString(",\n")),
            "};"
          )
        )
      )
    )
  }

  private def getPortHistoryFunctions: List[CppDoc.Class.Member] = {
    functionClassMember(
      Some("Clear from port history"),
      "clearFromPortHistory",
      Nil,
      CppDoc.Type("void"),
      List.concat(
        lines("this->fromPortHistorySize = 0;"),
        typedOutputPorts.map(p =>
          line(s"this->${fromPortHistoryName(p.getUnqualifiedName)}->clear();")
        )
      )
    ) ::
      typedOutputPorts.map(p =>
        functionClassMember(
          Some(s"Push an entry on the history for ${inputPortName(p.getUnqualifiedName)}"),
          fromPortPushEntryName(p.getUnqualifiedName),
          getPortFunctionParams(p),
          CppDoc.Type("void"),
          List.concat(
            wrapInScope(
              s"${fromPortEntryName(p.getUnqualifiedName)} _e = {",
              lines(getPortParams(p).map(_._1).mkString(",\n")),
              "};"
            ),
            lines(
              s"""|this->${fromPortHistoryName(p.getUnqualifiedName)}->push_back(_e);
                  |this->fromPortHistorySize++;
                  |"""
            )
          )
        )
      )
  }

  private def getPortHistoryVariables: List[CppDoc.Class.Member] = {
    List(
      linesClassMember(
        List.concat(
          lines(
            s"""|//! The total number of port entries
                |U32 fromPortHistorySize;
                |"""
          ),
          typedOutputPorts.flatMap(p =>
            lines(
              s"""|//! The history for ${inputPortName(p.getUnqualifiedName)}
                  |History<${fromPortEntryName(p.getUnqualifiedName)}>* ${fromPortHistoryName(p.getUnqualifiedName)};
                  |"""
            )
          )
        ),
        CppDoc.Lines.Hpp
      )
    )
  }

  private def getCmdHistoryTypes: List[CppDoc.Class.Member] = {
    List(
      linesClassMember(
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
  }

  private def getCmdHistoryVariables: List[CppDoc.Class.Member] = {
    List(
      linesClassMember(
        lines(
          """|//! The command response history
             |History<CmdResponse>* cmdResponseHistory;
             |"""
        ),
        CppDoc.Lines.Hpp
      )
    )
  }

}
