package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology private functions */
case class TopPrivateFunctions(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  def getLines: List[Line] = addBannerComment(
    "Private functions",
    List(
      addComment("Initialize components", getInitComponentsLines),
      addComment("Configure components", getConfigComponentsLines),
      addComment("Set component base IDs", getSetBaseIDsLines),
      addComment("Connect components", getConnectComponentsLines),
      addComment("Register commands", getRegCommandsLines),
      addComment("Load parameters", getLoadParametersLines),
      addComment("Start tasks", getStartTasksLines),
      addComment("Stop tasks", getStopTasksLines),
      addComment("Free threads", getFreeThreadsLines),
      addComment("Tear down components", getTearDownComponentsLines),
    ).flatten
  )

  private def getInitComponentsLines: List[Line] = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val name = getNameAsIdent(ci.qualifiedName)
      getCodeLinesForPhase (CppWriter.Phases.initComponents) (ci).getOrElse(
        ci.component.aNode._2.data.kind match {
          case Ast.ComponentKind.Passive => 
            lines(s"$name.init(InstanceIDs::$name);")
          case _ =>
            lines(s"$name.init(QueueSizes::$name, InstanceIDs::$name);")
        }
      )
    }
    wrapInScope(
      "void initComponents(const TopologyState& state) {",
      instances.flatMap(getCode),
      "}"
    )
  }

  private def getConfigComponentsLines: List[Line] = {
    def getCode(ci: ComponentInstance): List[Line] = {
      val name = getNameAsIdent(ci.qualifiedName)
      getCodeLinesForPhase (CppWriter.Phases.configComponents) (ci).getOrElse(Nil)
    }
    wrapInScope(
      "void configComponents(const TopologyState& state) {",
      instances.flatMap(getCode),
      "}"
    )
  }

  private def getSetBaseIDsLines: List[Line] =
    wrapInScope(
      "void setBaseIds() {",
      instances.map(ci => {
        val name = getNameAsIdent(ci.qualifiedName)
        val id = CppWriter.writeId(ci.baseId)
        line(s"$name.setidBase($id);")
      }),
      "}"
    )

  private def getConnectComponentsLines: List[Line] = {
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
          s"${in._1}_get_${in._2}_InputPort(${in._3})"
        ).map(line),
        ");"
      )
    }
    wrapInScope(
      "void connectComponents() {",
      t.connectionMap.toList.flatMap { 
        case (name, cs) => addBlankPostfix(
          addComment(name, cs.flatMap(writeConnection))
        )
      },
      "}"
    )
  }

  private def getRegCommandsLines: List[Line] = {
    def getCode(ci: ComponentInstance): List[Line] = {
      getCodeLinesForPhase (CppWriter.Phases.regCommands) (ci).getOrElse(
        ci.component.commandMap.size match {
          case 0 => Nil
          case _ =>
            val name = getNameAsIdent(ci.qualifiedName)
            lines(s"$name.regCommands();")
        }
      )
    }
    wrapInScope(
      "void regCommands() {",
      instances.flatMap(getCode),
      "}"
    )
  }

  private def getLoadParametersLines: List[Line] = {
    def getCode(ci: ComponentInstance): List[Line] = {
      getCodeLinesForPhase (CppWriter.Phases.loadParameters) (ci).getOrElse(
        ci.component.paramMap.size match {
          case 0 => Nil
          case _ =>
            val name = getNameAsIdent(ci.qualifiedName)
            lines(s"$name.loadParameters();")
        }
      )
    }
    wrapInScope(
      "void loadParameters() {",
      instances.flatMap(getCode),
      "}"
    )
  }

  private def getStartTasksLines: List[Line] = {
    // TODO
    Nil
  }

  private def getStopTasksLines: List[Line] = {
    // TODO
    Nil
  }

  private def getFreeThreadsLines: List[Line] = {
    // TODO
    Nil
  }

  private def getTearDownComponentsLines: List[Line] = {
    // TODO
    Nil
  }

}
