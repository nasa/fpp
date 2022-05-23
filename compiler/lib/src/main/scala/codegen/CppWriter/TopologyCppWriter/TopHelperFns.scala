package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology helper functions */
case class TopHelperFns(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  /** Compute the set of defined function names and the list
   *  of CppDoc members defining the functions */
  def getMembers: (Set[String], List[CppDoc.Member]) = {
    // Get pairs of (function name, function member)
    val pairs = List(
      getInitComponentsFn,
      getConfigComponentsFn,
      getSetBaseIdsFn,
      getConnectComponentsFn,
      getRegCommandsFn,
      getReadParametersFn,
      getLoadParametersFn,
      getStartTasksFn,
      getStopTasksFn,
      getFreeThreadsFn,
      getTearDownComponentsFn,
    )
    // Compute the set of names with nonempty lines
    val fnNames = pairs.foldLeft (Set[String]()) {
      case (set, (_, None)) => set
      case (set, (name, _)) => set + name
    }
    // Add the banner comment
    val fnMembers = pairs.map(_._2).filter(_.isDefined).map(_.get)
    val members = fnMembers match {
      case Nil => Nil
      case _ => getBannerComment :: fnMembers
    }
    (fnNames, members)
  }

  private val stateParams = List(
    CppDoc.Function.Param(
      CppDoc.Type("const TopologyState&"),
      "state",
      Some("The topology state")
    )
  )

  private def getInitComponentsFn = {
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
    val memberOpt = getFnMemberOpt(
      "Initialize components",
      name,
      stateParams,
      instances.flatMap(getCode)
    )
    (name, memberOpt)
  }

  private def getConfigComponentsFn = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val name = getNameAsIdent(ci.qualifiedName)
      getCodeLinesForPhase (CppWriter.Phases.configComponents) (ci).getOrElse(Nil)
    }
    val name = "configComponents"
    val memberOpt = getFnMemberOpt(
      "Configure components",
      name,
      stateParams,
      instances.flatMap(getCode)
    )
    (name, memberOpt)
  }

  private def getSetBaseIdsFn = {
    val name = "setBaseIds"
    val body = instancesByBaseId.map(ci => {
      val name = getNameAsIdent(ci.qualifiedName)
      line(s"$name.setIdBase(BaseIds::$name);")
    })
    val memberOpt = getFnMemberOpt(
      "Set component base Ids",
      name,
      Nil,
      body
    )
    (name, memberOpt)
  }

  private def getConnectComponentsFn = {
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
    val body = t.connectionMap.toList.sortWith(_._1 < _._1).flatMap {
      case (name, cs) => addComment(
        name,
        t.sortConnections(cs).flatMap(writeConnection).toList
      )
    }
    val memberOpt = getFnMemberOpt(
      "Connect components",
      name,
      Nil,
      body
    )
    (name, memberOpt)
  }

  private def getRegCommandsFn = {
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
    val memberOpt = getFnMemberOpt(
      "Register commands",
      name,
      Nil,
      instances.flatMap(getCode)
    )
    (name, memberOpt)
  }

  private def getReadParametersFn = {
    def getCode(ci: ComponentInstance): List[Line] =
      getCodeLinesForPhase (CppWriter.Phases.readParameters) (ci).getOrElse(Nil)
    val name = "readParameters"
    val memberOpt = getFnMemberOpt(
      "Read parameters",
      name,
      Nil,
      instances.flatMap(getCode)
    )
    (name, memberOpt)
  }

  private def getLoadParametersFn = {
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
    val memberOpt = getFnMemberOpt(
      "Load parameters",
      name,
      Nil,
      instances.flatMap(getCode)
    )
    (name, memberOpt)
  }

  private def getStartTasksFn = {
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
    val memberOpt = getFnMemberOpt(
      "Start tasks",
      name,
      stateParams,
      instances.flatMap(getCode)
    )
    (name, memberOpt)
  }

  private def getStopTasksFn = {
    def getCode(ci: ComponentInstance): List[Line] =
      getCodeLinesForPhase (CppWriter.Phases.stopTasks) (ci).getOrElse {
        if (isActive(ci)) {
          val name = getNameAsIdent(ci.qualifiedName)
          lines(s"$name.exit();")
        }
        else Nil
      }
    val name = "stopTasks"
    val memberOpt = getFnMemberOpt(
      "Stop tasks",
      name,
      stateParams,
      instances.flatMap(getCode)
    )
    (name, memberOpt)
  }

  private def getFreeThreadsFn = {
    def getCode(ci: ComponentInstance): List[Line] =
      getCodeLinesForPhase (CppWriter.Phases.freeThreads) (ci).getOrElse {
        if (isActive(ci)) {
          val name = getNameAsIdent(ci.qualifiedName)
          lines(s"(void) $name.ActiveComponentBase::join(nullptr);")
        }
        else Nil
      }
    val name = "freeThreads"
    val memberOpt = getFnMemberOpt(
      "Free threads",
      name,
      stateParams,
      instances.flatMap(getCode)
    )
    (name, memberOpt)
  }

  private def getTearDownComponentsFn = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val name = getNameAsIdent(ci.qualifiedName)
      getCodeLinesForPhase (CppWriter.Phases.tearDownComponents) (ci).getOrElse(Nil)
    }
    val name = "tearDownComponents"
    val memberOpt = getFnMemberOpt(
      "Tear down components",
      name,
      stateParams,
      instances.flatMap(getCode)
    )
    (name, memberOpt)
  }

  private def getFnMemberOpt(
    comment: String,
    name: String,
    params: List[CppDoc.Function.Param],
    body: List[Line]
  ) = body match {
    case Nil => None
    case ll => Some(
      CppDoc.Member.Function(
        CppDoc.Function(
          Some(comment),
          name,
          params,
          CppDoc.Type("void"),
          ll
        )
      )
    )
  }

  private def getBannerComment = CppDoc.Member.Lines(
    CppDoc.Lines(
      CppDocWriter.writeBannerComment("Helper functions"),
      CppDoc.Lines.Both
    )
  )

}
