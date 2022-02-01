package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology private functions */
case class TopPrivateFunctions(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  /** Compute the set of defined function names and the list
   *  of lines defining the functions */
  def getLines: (Set[String], List[Line]) = {
    // Get pairs of (function name, function lines)
    val pairs = List(
      getInitComponentsLines,
      getConfigComponentsLines,
      getSetBaseIdsLines,
      getConnectComponentsLines,
      getRegCommandsLines,
      getReadParametersLines,
      getLoadParametersLines,
      getStartTasksLines,
      getStopTasksLines,
      getFreeThreadsLines,
      getTearDownComponentsLines,
    )
    // Compute the set of names with nonempty lines
    val fns = pairs.foldLeft (Set[String]()) {
      case (set, (_, Nil)) => set
      case (set, (name, _)) => set + name
    }
    // Extract the lines
    val ll = addBannerComment(
      "Private functions",
      pairs.map(_._2).flatten
    )
    (fns, ll)
  }

  private def getInitComponentsLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val name = getNameAsIdent(ci.qualifiedName)
      getCodeLinesForPhase (CppWriter.Phases.initComponents) (ci).getOrElse(
        ci.component.aNode._2.data.kind match {
          case Ast.ComponentKind.Passive => 
            lines(s"$name.init(InstanceIds::$name);")
          case _ =>
            lines(s"$name.init(QueueSizes::$name, InstanceIds::$name);")
        }
      )
    }
    val name = "initComponents"
    val ll = addComment(
      "Initialize components",
      wrapInScope(
        s"void $name(const TopologyState& state) {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

  private def getConfigComponentsLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val name = getNameAsIdent(ci.qualifiedName)
      getCodeLinesForPhase (CppWriter.Phases.configComponents) (ci).getOrElse(Nil)
    }
    val name = "configComponents"
    val ll = addComment(
      "Configure components",
      wrapInScope(
        s"void $name(const TopologyState& state) {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

  private def getSetBaseIdsLines: (String, List[Line]) = {
    val name = "setBaseIds"
    val ll = addComment(
      "Set component base Ids",
      wrapInScope(
        s"void $name() {",
        instancesByBaseId.map(ci => {
          val name = getNameAsIdent(ci.qualifiedName)
          line(s"$name.setIdBase(BaseIds::$name);")
        }),
        "}"
      )
    )
    (name, ll)
  }

  private def getConnectComponentsLines: (String, List[Line]) = {
    def getPortInfo(pii: PortInstanceIdentifier, c: Connection) = {
      val instanceName = getNameAsIdent(pii.componentInstance.qualifiedName)
      val portName = pii.portInstance.getUnqualifiedName
      val portNumber = t.getPortNumber(pii.portInstance, c).get
      (instanceName, portName, portNumber)
    }
    def writeConnection(c: Connection) = {
      val out = getPortInfo(c.from.port, c)
      val in = getPortInfo(c.to.port, c)
      wrapInScope(
        s"${out._1}.set_${out._2}_OutputPort(",
        List(
          s"${out._3},",
          s"${in._1}.get_${in._2}_InputPort(${in._3})"
        ).map(line).map(indentIn),
        ");"
      )
    }
    val name = "connectComponents"
    val ll = addComment(
      "Connect components",
      wrapInScope(
        s"void $name() {",
        addBlankPostfix(
          t.connectionMap.toList.sortWith(_._1 < _._1).flatMap {
            case (name, cs) => addComment(
              name,
              t.sortConnections(cs).flatMap(writeConnection).toList
            )
          }
        ),
        "}"
      )
    )
    (name, ll)
  }

  private def getRegCommandsLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] = {
      getCodeLinesForPhase (CppWriter.Phases.regCommands) (ci).getOrElse(
        if (hasCommands(ci)) {
          val name = getNameAsIdent(ci.qualifiedName)
          lines(s"$name.regCommands();")
        }
        else Nil
      )
    }
    val name = "regCommands"
    val ll = addComment(
      "Register commands",
      wrapInScope(
        s"void $name() {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

  private def getReadParametersLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] =
      getCodeLinesForPhase (CppWriter.Phases.readParameters) (ci).getOrElse(Nil)
    val name = "readParameters"
    val ll = addComment(
      "Read parameters",
      wrapInScope(
        s"void $name() {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

  private def getLoadParametersLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] = {
      getCodeLinesForPhase (CppWriter.Phases.loadParameters) (ci).getOrElse(
        if (hasParams(ci)) {
          val name = getNameAsIdent(ci.qualifiedName)
          lines(s"$name.loadParameters();")
        }
        else Nil
      )
    }
    val name = "loadParameters"
    val ll = addComment(
      "Load parameters",
      wrapInScope(
        s"void $name() {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

  private def getStartTasksLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] =
      getCodeLinesForPhase (CppWriter.Phases.startTasks) (ci).getOrElse {
        if (isActive(ci)) {
          val name = getNameAsIdent(ci.qualifiedName)
          val priority = ci.priority match {
            case Some(_) => s"static_cast<NATIVE_UINT_TYPE>(Priorities::$name),"
            case None => "Os::Task::TASK_DEFAULT, // Default priority"
          }
          val stackSize = ci.stackSize match {
            case Some(_) => s"static_cast<NATIVE_UINT_TYPE>(StackSizes::$name),"
            case None => "Os::Task::TASK_DEFAULT, // Default stack size"
          }
          val cpu = ci.cpu match {
            case Some(_) => s"static_cast<NATIVE_UINT_TYPE>(CPUs::$name),"
            case None => "Os::Task::TASK_DEFAULT, // Default CPU"
          }
          wrapInScope(
            s"$name.start(",
            (
              List(
                priority,
                stackSize,
                cpu,
                s"static_cast<NATIVE_UINT_TYPE>(TaskIds::$name)",
              )
            ).map(line),
            ");"
          )
        }
        else Nil
      }
    val name = "startTasks"
    val ll = addComment(
      "Start tasks",
      wrapInScope(
        s"void $name(const TopologyState& state) {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

  private def getStopTasksLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] =
      getCodeLinesForPhase (CppWriter.Phases.stopTasks) (ci).getOrElse {
        if (isActive(ci)) {
          val name = getNameAsIdent(ci.qualifiedName)
          lines(s"$name.exit();")
        }
        else Nil
      }
    val name = "stopTasks"
    val ll = addComment(
      "Stop tasks",
      wrapInScope(
        "void stopTasks(const TopologyState& state) {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

  private def getFreeThreadsLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] =
      getCodeLinesForPhase (CppWriter.Phases.freeThreads) (ci).getOrElse {
        if (isActive(ci)) {
          val name = getNameAsIdent(ci.qualifiedName)
          lines(s"(void) $name.ActiveComponentBase::join(nullptr);")
        }
        else Nil
      }
    val name = "freeThreads"
    val ll = addComment(
      "Free threads",
      wrapInScope(
        s"void $name(const TopologyState& state) {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

  private def getTearDownComponentsLines: (String, List[Line]) = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val name = getNameAsIdent(ci.qualifiedName)
      getCodeLinesForPhase (CppWriter.Phases.tearDownComponents) (ci).getOrElse(Nil)
    }
    val name = "tearDownComponents"
    val ll = addComment(
      "Tear down components",
      wrapInScope(
        s"void $name(const TopologyState& state) {",
        instances.flatMap(getCode),
        "}"
      )
    )
    (name, ll)
  }

}
