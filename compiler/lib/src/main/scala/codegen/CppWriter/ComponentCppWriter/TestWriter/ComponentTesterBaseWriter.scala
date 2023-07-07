package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out C++ for component test harness base classes */
case class ComponentTesterBaseWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  private val fileName = ComputeCppFiles.FileNames.getComponentTesterBase(name)

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name component test harness base class",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val cls = classMember(
      Some(
        s"\\class $testerBaseClassName\n\\brief Auto-generated base for $name component test harness",
      ),
      testerBaseClassName,
      Some("public Fw::PassiveComponentBase"),
      getClassMembers
    )
    List.concat(
      List(getHppIncludes, getCppIncludes),
      wrapInNamespaces(namespaceIdentList, List(cls))
    )
  }

  private def getHppIncludes: CppDoc.Member = {
    val userHeaders = List(
      s"${s.getRelativePath(ComputeCppFiles.FileNames.getComponent(name)).toString}.hpp",
      "Fw/Types/Assert.hpp",
      "Fw/Comp/PassiveComponentBase.hpp",
      "Fw/Port/InputSerializePort.hpp"
    ).sorted.map(CppWriter.headerString).map(line)
    val systemHeader = lines(CppWriter.systemHeaderString("cstdio"))
    linesMember(
      List.concat(
        Line.blank :: systemHeader,
        Line.blank :: userHeaders
      )
    )
  }

  private def getCppIncludes: CppDoc.Member = {
    val userHeader = lines(CppWriter.headerString(s"${s.getRelativePath(fileName).toString}.hpp"))
    val systemHeaders = List(
      "cstdlib",
      "cstring"
    ).sorted.map(CppWriter.systemHeaderString).map(line)
    linesMember(
      List.concat(
        Line.blank :: systemHeaders,
        Line.blank :: userHeader
      ),
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] = {
    List.concat(
      // Public members
      getInitFunction,

      // Protected members
      getConstructorMembers,
    )
  }

  private def getInitFunction: List[CppDoc.Class.Member] = {
    def writePortConnections(port: PortInstance) =
      ComponentCppWriter.writePortConnections(
        port,
        portNumGetterName,
        portVariableName,
        fromPortCallbackName,
        portName,
        true
      )

    val body = intersperseBlankLines(
      List(
        sortedParams.map((_, param) => line(
          s"this->${paramValidityFlagName(param.getName)} = Fw::ParamValid::UNINIT;"
        )),
        lines(
          """|// Initialize base class
             |Fw::PassiveComponentBase::init(instance);
             |"""
        ),
        intersperseBlankLines(specialOutputPorts.map(writePortConnections)),
        intersperseBlankLines(typedOutputPorts.map(writePortConnections)),
        intersperseBlankLines(serialOutputPorts.map(writePortConnections)),
        intersperseBlankLines(specialInputPorts.map(writePortConnections)),
        intersperseBlankLines(typedInputPorts.map(writePortConnections)),
        intersperseBlankLines(serialInputPorts.map(writePortConnections)),
      )
    )

    addAccessTagAndComment(
      "public",
      "Component initialization",
      List(
        functionClassMember(
          Some(s"Initialize object $testerBaseClassName"),
          "init",
          List(
            CppDoc.Function.Param(
              CppDoc.Type("NATIVE_INT_TYPE"),
              "instance",
              Some("The instance number"),
              Some("0")
            )
          ),
          CppDoc.Type("void"),
          body,
          CppDoc.Function.Virtual
        )
      )
    )
  }

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "protected",
      "Component construction and destruction",
      List(
        constructorClassMember(
          Some(s"Construct object $testerBaseClassName"),
          List(
            CppDoc.Function.Param(
              CppDoc.Type("const char* const"),
              "compName",
              Some("The component name")
            ),
            CppDoc.Function.Param(
              CppDoc.Type("U32"),
              "maxHistorySize",
              Some("The maximum size of each history")
            )
          ),
          List(
            "Fw::PassiveComponentBase(compName)"
          ),
          intersperseBlankLines(
            List(
              if typedOutputPorts.nonEmpty then line("// Initialize port histories") ::
                typedOutputPorts.map(p => line(
                  s"this->${fromPortHistoryName(p.getUnqualifiedName)} = new History<${fromPortEntryName(p.getUnqualifiedName)}>(maxHistorySize);"
                ))
              else Nil,
              if hasCommands || hasParameters then lines(
                """|// Initialize command history
                   |this->cmdResponseHistory = new History<CmdResponse>(maxHistorySize);
                   |"""
              )
              else Nil,
              if hasEvents then List.concat(
                lines(
                  """|// Initialize event histories
                     |#if FW_ENABLE_TEXT_LOGGING
                     |this->textLogHistory = new History<TextLogEntry>(maxHistorySize);
                     |#endif
                     |"""
                ),
                sortedEvents.map((_, event) => line(
                  s"this->${eventHistoryName(event.getName)} = new History<${eventEntryName(event.getName)}>(maxHistorySize);"
                ))
              )
              else Nil,
              if hasChannels then line("// Initialize telemetry histories") ::
                sortedChannels.map((_, channel) => line(
                  s"this->${tlmHistoryName(channel.getName)} = new History<${tlmEntryName(channel.getName)}>(maxHistorySize);"
                ))
              else Nil,
              if hasHistories then lines(
                """|// Clear history
                   |this->clearHistory();
                   |"""
              )
              else Nil,
            )
          )
        ),
        destructorClassMember(
          Some(s"Destroy object $testerBaseClassName"),
          intersperseBlankLines(
            List(
              if typedOutputPorts.nonEmpty then line("// Destroy port histories") ::
                typedOutputPorts.map(p => line(
                  s"delete this->${fromPortHistoryName(p.getUnqualifiedName)};"
                ))
              else Nil,
              if hasCommands || hasParameters then lines(
                """|// Destroy command history
                   |delete this->cmdResponseHistory;
                   |"""
              )
              else Nil,
              if hasEvents then List.concat(
                lines(
                  """|// Destroy event histories
                     |#if FW_ENABLE_TEXT_LOGGING
                     |delete this->textLogHistory;
                     |#endif
                     |"""
                ),
                sortedEvents.map((_, event) => line(
                  s"delete this->${eventHistoryName(event.getName)};"
                ))
              )
              else Nil,
              if hasChannels then line("// Destroy telemetry histories") ::
                sortedChannels.map((_, channel) => line(
                  s"delete this->${tlmHistoryName(channel.getName)};"
                ))
              else Nil,
            )
          ),
          CppDoc.Class.Destructor.Virtual
        )
      )
    )
  }

}
