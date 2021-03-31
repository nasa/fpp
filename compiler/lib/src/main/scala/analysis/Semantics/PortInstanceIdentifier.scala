package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP port instance identifier */
case class PortInstanceIdentifier(
  /** The component instance */
  componentInstance: ComponentInstance,
  /** The port instance */
  portInstance: PortInstance
) {

  /** Gets the qualified name */
  def getQualifiedName = {
    val componentName = componentInstance.qualifiedName
    val identList = componentName.toIdentList
    Name.Qualified.fromIdentList(identList :+ portInstance.getUnqualifiedName)
  }

  /** Gets the unqualified name */
  def getUnqualifiedName = {
    val componentName = componentInstance.getUnqualifiedName
    val portName = portInstance.getUnqualifiedName
    val identList = List(componentName, portName)
    Name.Qualified.fromIdentList(identList)
  }

  /** Get this endpoint of a port connection at a port instance */
  def getThisEndpoint(c: Connection) = {
    import PortInstance.Direction._
    portInstance.getDirection.get match {
      case Input => c.to
      case Output => c.from
    }
  }

  /** Get the other endpoint of a port connection at a port instance */
  def getOtherEndpoint(c: Connection) = {
    import PortInstance.Direction._
    portInstance.getDirection.get match {
      case Input => c.from
      case Output => c.to
    }
  }

}

object PortInstanceIdentifier {

  /** Creates a port instance identifier from an AST node */
  def fromNode(a: Analysis, node: AstNode[Ast.PortInstanceIdentifier]):
    Result.Result[PortInstanceIdentifier] = {
      val data = node.data
      for {
        componentInstance <- a.getComponentInstance(
          data.componentInstance.id
        )
        portInstance <- componentInstance.component.getPortInstance(
          data.portName
        )
      }
      yield PortInstanceIdentifier(
        componentInstance,
        portInstance
      )
    }

}
