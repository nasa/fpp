package fpp.compiler.codegen

import fpp.compiler.analysis._
import fpp.compiler.ast._
import fpp.compiler.util._

/** Writes out C++ for topology-dependent component implementation */
case class TopComponents(
  s: CppWriterState,
  aNode: Ast.Annotated[AstNode[Ast.DefTopology]]
) extends TopologyCppWriterUtils(s, aNode) {

  private val componentMap = t.connectionMap.values.foldLeft
    (Map(): TopComponents.ComponentMap)
    (addConnectionsToMap)

  private val sortedComponentList = componentMap.toList.sortWith {
    case ((c1, _), (c2, _)) =>
      val name1 = s.a.getQualifiedName(Symbol.Component(c1.aNode))
      val name2 = s.a.getQualifiedName(Symbol.Component(c2.aNode))
      name1.toString < name2.toString
  }

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
    sortedComponentList.flatMap(getMembersForComponent)

  private def getMembersForComponent(
    c: Component,
    pnm: TopComponents.PortNameMap
  ) = {
    val t = s.a.topologyMap(Symbol.Topology(aNode))
    val writer = TopComponentCppWriter(s, c.aNode, t, pnm)
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
