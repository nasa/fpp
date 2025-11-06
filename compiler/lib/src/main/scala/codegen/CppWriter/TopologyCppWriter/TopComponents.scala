package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology-dependent component implementation */
case class TopComponents(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  val componentMap = t.connectionMap.values.foldLeft
    (Map(): TopComponents.ComponentMap)
    (addConnectionsToMap)

  def getMembers: List[CppDoc.Member] = 
    wrapMembersInIfDirective(
      "#ifdef FW_DIRECT_PORT_CALLS",
      addMemberComment(
        "Topology-dependent component implementation",
        getComponentMembers,
        CppDoc.Lines.Cpp
      ),
      CppDoc.Lines.Cpp
    )

  private def getComponentMembers =
    componentMap.toList.flatMap(getMembersForComponent)

  private def getMembersForComponent(
    c: Component,
    pnm: TopComponents.PortNameMap
  ) = {
    val writer = TopComponentCppWriter(s, c.aNode, pnm)
    val qualifiedName = s.a.getQualifiedName(Symbol.Component(c.aNode))
    val member = linesMember(
      Line.blank ::
      wrapInNamespaceLines(
        qualifiedName.qualifier,
        List.concat(
          writer.writeIsConnectedFns,
          writer.writeInvocationFns,
          List(Line.blank)
        )
      ),
      CppDoc.Lines.Cpp
    )
    List(member)
  }

  private def addConnectionsToMap(
    cm: TopComponents.ComponentMap,
    cs: List[Connection]
  ) = cs.foldLeft (cm) (addConnectionToMap)

  private def addConnectionToMap(
    componentMap: TopComponents.ComponentMap,
    connection: Connection
  ) = {
    val port = connection.from.port
    val componentInstance = port.componentInstance
    val portInstance = port.portInstance
    val component = componentInstance.component
    val portName = portInstance.getUnqualifiedName
    val portNumber = t.fromPortNumberMap(connection)
    val portNameMap = componentMap.get(component).getOrElse(Map())
    val componentInstanceMap = portNameMap.get(portName).getOrElse(Map())
    val portNumberMap = componentInstanceMap.get(componentInstance).getOrElse(Map())
    val portNumberMap1 = portNumberMap + (portNumber -> connection)
    val componentInstanceMap1 = componentInstanceMap + (componentInstance -> portNumberMap1)
    val portNameMap1 = portNameMap + (portName -> componentInstanceMap1)
    componentMap + (component -> portNameMap1)
  }

}

object TopComponents {

  /** port number -> connection */
  type PortNumberMap = Map[Int, Connection]

  /** component instance -> port number -> connection */
  type ComponentInstanceMap = Map[ComponentInstance, PortNumberMap]

  /** output port name -> component instance -> port number -> connection */
  type PortNameMap = Map[Name.Unqualified, ComponentInstanceMap]

  /** component -> output port name -> component instance -> port number -> connection */
  type ComponentMap = Map[Component, PortNameMap]

}
