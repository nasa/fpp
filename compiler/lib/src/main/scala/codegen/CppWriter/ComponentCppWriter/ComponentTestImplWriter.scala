package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out C++ for component test harness implementation classes */
case class ComponentTestImplWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentTestUtils(s, aNode) {

  private val fileName = ComputeCppFiles.FileNames.getComponentTestImpl(name)

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name test harness implementation class",
      fileName,
      includeGuard,
      getMembers,
      s.toolName
    )
  }

  private def getMembers: List[CppDoc.Member] = {
    val cls = classMember(
      None,
      testImplClassName,
      Some(s"public $gTestClassName"),
      getClassMembers
    )
    List.concat(
      List(getHppIncludes, getCppIncludes),
      wrapInNamespaces(namespaceIdentList, List(cls))
    )
  }

  private def getHppIncludes: CppDoc.Member = {
    val headers = List(
      s"${s.getRelativePath(ComputeCppFiles.FileNames.getComponentGTestBase(name))}.hpp",
      s"${s.getRelativePath(ComputeCppFiles.FileNames.getComponentImpl(name))}.hpp"
    )
    linesMember(
      addBlankPrefix(headers.map(CppWriter.headerString).map(line))
    )
  }

  private def getCppIncludes: CppDoc.Member = {
    val header = s"${s.getRelativePath(fileName).toString}.hpp"
    linesMember(
      addBlankPrefix(lines(CppWriter.headerString(header))),
      CppDoc.Lines.Cpp
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] = {
    List.concat(
      getConstantMembers,
      getConstructorMembers,
      getPortHandlers(typedOutputPorts),
      getPortHandlers(serialOutputPorts),
      getHelpers,
      getVariableMembers
    )
  }

  private def getConstantMembers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "public",
      "Constants",
      List(
        linesClassMember(
          List.concat(
            Line.blank :: lines(
              s"""|// Maximum size of histories storing events, telemetry, and port outputs
                  |static const NATIVE_INT_TYPE $historySizeConstantName = 10;
                  |
                  |// Instance ID supplied to the component instance under test
                  |static const NATIVE_INT_TYPE $idConstantName = 0;
                  |"""
            ),
            if data.kind != Ast.ComponentKind.Passive then lines(
              s"""|
                  |// Queue depth supplied to the component instance under test
                  |static const NATIVE_INT_TYPE $queueDepthConstantName = 10;
                  |"""
            )
            else Nil
          )
        )
      ),
      CppDoc.Lines.Hpp
    )
  }

  private def getConstructorMembers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "public",
      "Construction and destruction",
      List(
        constructorClassMember(
          Some(s"Construct object $testImplClassName"),
          Nil,
          List(
            s"$gTestClassName(\"$testImplClassName\", $testImplClassName::MAX_HISTORY_SIZE)",
            s"component(\"$implClassName\")"
          ),
          lines(
            """|this->initComponents();
               |this->connectPorts();
               |"""
          )
        ),
        destructorClassMember(
          Some(s"Destroy object $testImplClassName"),
          Nil
        )
      )
    )
  }

  private def getPortHandlers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "private",
      s"Handlers for ${getPortListTypeString(ports)} from ports",
      ports.map(p => {
        val todoMsg = getPortReturnType(p) match {
          case Some(_) => "// TODO return"
          case None => "// TODO"
        }

        functionClassMember(
          Some(s"Handler implementation for ${p.getUnqualifiedName}"),
          fromPortHandlerName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          getPortReturnTypeAsCppDocType(p),
          lines(todoMsg)
        )
      })
    )
  }

  private def getHelpers: List[CppDoc.Class.Member] = {
    def writeConnection(p: PortInstance, portNum: String) =
      p.getDirection.get match {
        case PortInstance.Direction.Input => lines(
          s"""|this->${toPortConnectorName(p.getUnqualifiedName)}(
              |  $portNum,
              |  this->component.${inputPortGetterName(p.getUnqualifiedName)}($portNum)
              |);
              |"""
        )
        case PortInstance.Direction.Output => lines(
          s"""|this->component.${outputPortConnectorName(p.getUnqualifiedName)}(
              |  $portNum,
              |  this->${fromPortGetterName(p.getUnqualifiedName)}($portNum)
              |);
              |"""
        )
      }
    def writeConnections(ports: List[PortInstance]) =
      ports match {
        case Nil => Nil
        case _ => List.concat(
          lines(
            s"// Connect ${getPortListTypeString(ports)} ${getPortListDirectionString(ports)} ports"
          ),
          Line.blank :: intersperseBlankLines(
            ports.map(p => p.getArraySize match {
              case 1 => writeConnection(p, "0")
              case size => wrapInForLoop(
                "NATIVE_UINT_TYPE i = 0",
                s"i < $size",
                "i++",
                writeConnection(p, "i")
              )
            })
          )
        )
      }

    val initArgs = List.concat(
      if data.kind != Ast.ComponentKind.Passive then List(s"$testImplClassName::$queueDepthConstantName")
      else Nil,
      List(s"$testImplClassName::$idConstantName"),
    ).mkString(", ")

    addAccessTagAndComment(
      "private",
      "Helper functions",
      List(
        functionClassMember(
          Some("Connect ports"),
          "connectPorts",
          Nil,
          CppDoc.Type("void"),
          intersperseBlankLines(
            List(
              writeConnections(typedInputPorts),
              writeConnections(typedOutputPorts),
              writeConnections(serialInputPorts),
              writeConnections(serialOutputPorts)
            )
          )
        ),
        functionClassMember(
          Some("Initialize components"),
          "initComponents",
          Nil,
          CppDoc.Type("void"),
          lines(
            s"""|this->init();
                |this->component.init($initArgs);
                |"""
          )
        )
      )
    )
  }

  private def getVariableMembers: List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "private",
      "Member variables",
      List(
        linesClassMember(
          Line.blank :: lines(
            s"""|//! The component under test
                |$implClassName component;
                |"""
          )
        )
      ),
      CppDoc.Lines.Hpp
    )
  }

}
