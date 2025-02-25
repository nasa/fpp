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

  private val internalSmWriter = ComponentInternalStateMachines(s, aNode)

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

  private def getActionImpl (sm: StateMachine) (action: StateMachineSymbol.Action):
  CppDoc.Class.Member = {
    val smSymbol = sm.getSymbol
    val smName = writeStateMachineImplType(sm.getSymbol)
    val actionName = action.getUnqualifiedName
    functionClassMember(
      Some(
        addSeparatedString(
          s"Implementation for action $actionName of state machine $smName",
          AnnotationCppWriter.asStringOpt(action.node)
        )
      ),
      internalSmWriter.getComponentActionFunctionName(smSymbol, action),
      internalSmWriter.getComponentActionFunctionParams(smSymbol, action),
      CppDoc.Type("void"),
      lines("// TODO"),
      CppDoc.Function.Override
    )
  }

  private def getActionImpls: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      "Implementations for internal state machine actions",
      internalSmSymbols.flatMap(getActionImplsForSm)
    )

  private def getActionImplsForSm(smSymbol: Symbol.StateMachine):
  List[CppDoc.Class.Member] = {
    val sm = s.a.stateMachineMap(smSymbol)
    sm.actions.map(getActionImpl (sm))
  }

  private def getClassMembers: List[CppDoc.Class.Member] =
    List.concat(
      getPublicMembers,
      getHandlers,
      getActionImpls,
      getGuardImpls,
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
      productRequestPort match {
        case None => Nil
        case _ => containersByName.map(
          (id, container) => getDpRecvHandler(container.getName, lines("// TODO"))
        )
      }
    )

  private def getDestructor: CppDoc.Class.Member =
    destructorClassMember(
      Some(s"Destroy $componentImplClassName object"),
      Nil,
    )

  private def getExternalSmOverflowHook(smi: StateMachineInstance):
  CppDoc.Class.Member =
    functionClassMember(
      Some(s"Overflow hook implementation for ${smi.getName}"),
      inputOverflowHookName(smi.getName, MessageType.StateMachine),
      ComponentExternalStateMachines.signalParams(s, smi.symbol),
      CppDoc.Type("void"),
      lines("// TODO"),
      CppDoc.Function.Override
    )

  private def getExternalSmOverflowHooks: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      "Overflow hook implementations for external state machines",
      externalStateMachineInstances.filter(_.queueFull == Ast.QueueFull.Hook).map(
        getExternalSmOverflowHook
      )
    )

  private def getGuardImpl (sm: StateMachine) (guard: StateMachineSymbol.Guard):
  CppDoc.Class.Member = {
    val smSymbol = sm.getSymbol
    val smName = writeStateMachineImplType(sm.getSymbol)
    val guardName = guard.getUnqualifiedName
    functionClassMember(
      Some(
        addSeparatedString(
          s"Implementation for guard $guardName of state machine $smName",
          AnnotationCppWriter.asStringOpt(guard.node)
        )
      ),
      internalSmWriter.getComponentGuardFunctionName(smSymbol, guard),
      internalSmWriter.getComponentGuardFunctionParams(smSymbol, guard),
      CppDoc.Type("bool"),
      lines("// TODO"),
      CppDoc.Function.Override,
      CppDoc.Function.Const
    )
  }

  private def getGuardImpls: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      "Implementations for internal state machine guards",
      internalSmSymbols.flatMap(getGuardImplsForSm)
    )

  private def getGuardImplsForSm(smSymbol: Symbol.StateMachine):
  List[CppDoc.Class.Member] = {
    val sm = s.a.stateMachineMap(smSymbol)
    sm.guards.map(getGuardImpl (sm))
  }

  private def getHandlers: List[CppDoc.Class.Member] =
    List.concat(
      getInputPortHandlers(typedInputPorts),
      getInputPortHandlers(serialInputPorts),
      getCommandHandlers,
      getInternalInterfaceHandlers,
      getDataProductHandlers
    )

  private def getHppIncludes: CppDoc.Member =
    linesMember(
      addBlankPrefix(s.writeIncludeDirectives(List(symbol)).map(line))
    )

  private def getInputPortHandler(pi: PortInstance): CppDoc.Class.Member = {
    val portName = pi.getUnqualifiedName
    val toDoMsg = getPortReturnType(pi) match {
      case Some(_) => "// TODO return"
      case None => "// TODO"
    }
    functionClassMember(
      Some(
        addSeparatedString(
          s"Handler implementation for $portName",
          getPortComment(pi)
        )
      ),
      inputPortHandlerName(portName),
      portNumParam :: getPortFunctionParams(pi),
      getPortReturnTypeAsCppDocType(pi),
      lines(toDoMsg),
      CppDoc.Function.Override
    )
  }

  private def getInputPortHandlers(ports: List[PortInstance]):
  List[CppDoc.Class.Member] = {
    val kind = getPortListTypeString(ports)
    addAccessTagAndComment(
      "PRIVATE",
      s"Handler implementations for $kind input ports",
      ports.map(getInputPortHandler)
    )
  }

  private def getInputPortOverflowHook(pi: PortInstance):
  CppDoc.Class.Member = {
    val portName = pi.getUnqualifiedName
    functionClassMember(
      Some(s"Overflow hook implementation for $portName"),
      inputOverflowHookName(portName, MessageType.Port),
      portNumParam :: getPortFunctionParams(pi),
      CppDoc.Type("void"),
      lines("// TODO"),
      CppDoc.Function.Override
    )
  }

  private def getInputPortOverflowHooks(ports: List[PortInstance]):
  List[CppDoc.Class.Member] = {
    val kind = getPortListTypeString(ports)
    addAccessTagAndComment(
      "PRIVATE",
      s"Overflow hook implementations for $kind input ports",
      ports.map(getInputPortOverflowHook)
    )
  }

  private def getInternalInterfaceHandler(pi: PortInstance.Internal):
  CppDoc.Class.Member = {
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

  private def getInternalSmOverflowHook(smi: StateMachineInstance):
  CppDoc.Class.Member =
    functionClassMember(
      Some(s"Overflow hook implementation for ${smi.getName}"),
      inputOverflowHookName(smi.getName, MessageType.StateMachine),
      ComponentInternalStateMachines.hookParams,
      CppDoc.Type("void"),
      lines("// TODO"),
      CppDoc.Function.Override
    )

  private def getInternalSmOverflowHooks: List[CppDoc.Class.Member] =
    addAccessTagAndComment(
      "PRIVATE",
      "Overflow hook implementations for internal state machines",
      internalSmWriter.hookInstances.map(getInternalSmOverflowHook)
    )

  private def getMembers: List[CppDoc.Member] = {
    val hppIncludes = getHppIncludes
    val cppIncludes = getCppIncludes
    val cls = classMember(
      None,
      componentImplClassName,
      Some(s"public $className"),
      getClassMembers,
      CppDoc.Class.Final
    )
    hppIncludes :: cppIncludes ::
    wrapInNamespaces(namespaceIdentList, List(cls))
  }

  private def getOverflowHooks: List[CppDoc.Class.Member] =
    List.concat(
      List(
        typedHookPorts,
        serialHookPorts,
        dataProductHookPorts
      ).flatMap(getInputPortOverflowHooks),
      getInternalPortOverflowHooks,
      getCommandOverflowHooks,
      getExternalSmOverflowHooks,
      getInternalSmOverflowHooks
    )

  private def getPublicMembers: List[CppDoc.Class.Member] =
    getConstructorsAndDestructors

}
