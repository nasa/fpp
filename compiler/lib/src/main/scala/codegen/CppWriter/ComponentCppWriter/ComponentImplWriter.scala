package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.codegen._
import fpp.compiler.util._

/** Writes out C++ for component implementation templates */
case class ComponentImplWriter(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefComponent]]
) extends ComponentCppWriterUtils(s, aNode) {

  private val className = componentClassName

  private val fileName = ComputeCppFiles.FileNames.getComponentImpl(componentName)

  private val name = componentName

  private val namespaceIdentList = componentNamespaceIdentList

  private val symbol = componentSymbol

  def write: CppDoc = {
    val includeGuard = s.includeGuardFromQualifiedName(symbol, fileName)
    CppWriter.createCppDoc(
      s"$name component implementation class",
      fileName,
      includeGuard,
      getMembers,
      s.toolName,
      "template.hpp",
      "template.cpp"
    )
  }

  private def getClassMembers: List[CppDoc.Class.Member] =
    List.concat(
      getPublicMembers,
      getHandlers,
      getOverflowHooks
    )

  private def getCommandHandler(opcode: Command.Opcode, cmd: Command.NonParam):
  CppDoc.Class.Member =
    functionClassMember(
      Some(
        addSeparatedString(
          s"Handler implementation for command ${cmd.getName}",
          AnnotationCppWriter.asStringOpt(cmd.aNode)
        )
      ),
      commandHandlerName(cmd.getName),
      opcodeParam ::cmdSeqParam :: cmdParamMap(opcode),
      CppDoc.Type("void"),
      lines(
        s"""|// TODO
            |this->cmdResponse_out(opCode, cmdSeq, Fw::CmdResponse::OK);"""
      ),
      CppDoc.Function.Override
    )

  private def getCommandHandlers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      s"Handler implementations for commands",
      nonParamCmds.map(getCommandHandler)
    )

  private def getCommandOverflowHook(opcode: Command.Opcode, cmd: Command):
  CppDoc.Class.Member =
    functionClassMember(
      Some(s"Overflow hook implementation for ${cmd.getName}"),
      inputOverflowHookName(cmd.getName, MessageType.Command),
      opcodeParam :: cmdSeqParam :: Nil,
      CppDoc.Type("void"),
      lines("// TODO"),
      CppDoc.Function.Override
    )

  private def getCommandOverflowHooks: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      "Overflow hook implementations for commands",
      hookCmds.map(getCommandOverflowHook)
    )

  private def getConstructor: CppDoc.Class.Member =
    constructorClassMember(
      Some(s"Construct $componentImplClassName object"),
      List(
        CppDoc.Function.Param(
          CppDoc.Type("const char* const"),
          "compName",
          Some("The component name")
        )
      ),
      List(s"$className(compName)"),
      Nil
    )

  private def getConstructorsAndDestructors: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "public",
      "Component construction and destruction",
      List(getConstructor, getDestructor)
    )

  private def getCppIncludes: CppDoc.Member = {
    val userHeaders = List(s.getIncludePath(symbol, fileName).toString)
    linesMember(
      addBlankPrefix(userHeaders.sorted.map(CppWriter.headerString).map(line)),
      CppDoc.Lines.Cpp
    )
  }

  private def getDataProductHandlers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      s"Handler implementations for data products",
      containersByName.map(
        (id, container) => getDpRecvHandler(container.getName, lines("// TODO"))
      )
    )

  private def getDestructor: CppDoc.Class.Member =
    destructorClassMember(
      Some(s"Destroy $componentImplClassName object"),
      Nil,
    )

  private def getHandlers: List[CppDoc.Class.Member] =
    List.concat(
      getPortHandlers(typedInputPorts),
      getPortHandlers(serialInputPorts),
      getCommandHandlers,
      getInternalInterfaceHandlers,
      getDataProductHandlers
    )

  private def getHppIncludes: CppDoc.Member =
    linesMember(
      addBlankPrefix(s.writeIncludeDirectives(List(symbol)).map(line))
    )

  private def getInputPortOverflowHooks(ports: List[PortInstance]): List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      s"Overflow hook implementations for 'hook' input ports",
      ports.map(p => {
        functionClassMember(
          Some(s"Overflow hook implementation for ${p.getUnqualifiedName}"),
          inputOverflowHookName(p.getUnqualifiedName, MessageType.Port),
          portNumParam :: getPortFunctionParams(p),
          CppDoc.Type("void"),
          lines("// TODO"),
          CppDoc.Function.Override
        )
      })
    )

  private def getInternalInterfaceHandler(pi: PortInstance.Internal): CppDoc.Class.Member = {
    val portName = pi.getUnqualifiedName
    functionClassMember(
      Some(
        addSeparatedString(
          s"Handler implementation for $portName",
          getPortComment(pi)
        )
      ),
      internalInterfaceHandlerName(portName),
      getPortFunctionParams(pi),
      CppDoc.Type("void"),
      lines("// TODO"),
      CppDoc.Function.Override
    )
  }

  private def getInternalInterfaceHandlers: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      s"Handler implementations for user-defined internal interfaces",
      internalPorts.map(getInternalInterfaceHandler)
    )

  private def getInternalPortOverflowHook(pi: PortInstance.Internal):
  CppDoc.Class.Member = {
    val portName = pi.getUnqualifiedName
    functionClassMember(
      Some(s"Overflow hook implementation for $portName"),
      inputOverflowHookName(portName, MessageType.Port),
      getPortFunctionParams(pi),
      CppDoc.Type("void"),
      lines("// TODO"),
      CppDoc.Function.Override
    )
  }

  private def getInternalPortOverflowHooks: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      s"Overflow hook implementations for internal ports",
      internalHookPorts.map(getInternalPortOverflowHook)
    )

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = classMember(
      None,
      componentImplClassName,
      Some(s"public $className"),
      getClassMembers
    )
    List.concat(
      List(hppIncludes, cppIncludes),
      wrapInNamespaces(namespaceIdentList, List(cls))
    )
  }

  private def getOverflowHooks: List[CppDoc.Class.Member] =
    List.concat(
      getInputPortOverflowHooks(
        List.concat(
          typedHookPorts,
          serialHookPorts,
          dataProductHookPorts
        )
      ),
      getInternalPortOverflowHooks,
      getCommandOverflowHooks,
      addAccessTagAndComment(
        "PRIVATE",
        "Overflow hook implementations for state machines",
        externalStateMachineInstances.filter(_.queueFull == Ast.QueueFull.Hook).map(
          smi => functionClassMember(
            Some(s"Overflow hook implementation for ${smi.getName}"),
            inputOverflowHookName(smi.getName, MessageType.StateMachine),
            ComponentExternalStateMachines.signalParams(s, smi.symbol),
            CppDoc.Type("void"),
            lines("// TODO"),
            CppDoc.Function.Override
          )
        )
      )
    )

  private def getPortHandlers(ports: List[PortInstance]): List[CppDoc.Class.Member] = {
    addAccessTagAndComment(
      "PRIVATE",
      s"Handler implementations for user-defined ${getPortListTypeString(ports)} input ports",
      ports.map(p => {
        val todoMsg = getPortReturnType(p) match {
          case Some(_) => "// TODO return"
          case None => "// TODO"
        }
        functionClassMember(
          Some(
            addSeparatedString(
              s"Handler implementation for ${p.getUnqualifiedName}",
              getPortComment(p)
            )
          ),
          inputPortHandlerName(p.getUnqualifiedName),
          portNumParam :: getPortFunctionParams(p),
          getPortReturnTypeAsCppDocType(p),
          lines(todoMsg),
          CppDoc.Function.Override
        )
      })
    )
  }

  private def getPublicMembers: List[CppDoc.Class.Member] =
    getConstructorsAndDestructors

}
