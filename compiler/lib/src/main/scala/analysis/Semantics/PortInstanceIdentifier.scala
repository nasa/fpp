package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/**
 * A generic trait used to emcompass the owner of connection endpoints
 * Can hold either topologies or components
 */
trait EndpointContainer {
  def getQualifiedName: Name.Qualified
  def getUnqualifiedName: String
}

/** Component port instance */
final case class PortInstanceIdentifier(
  /** The component instance */
  parent: EndpointContainer,
  /** The port instance */
  portInstance: PortInstance
) {

  override def toString = getQualifiedName.toString

  /** Gets the qualified name */
  def getQualifiedName: Name.Qualified = {
    val parentName = parent.getQualifiedName
    val identList = parentName.toIdentList
    Name.Qualified.fromIdentList(identList :+ portInstance.getUnqualifiedName)
  }

  /** Gets the unqualified name */
  def getUnqualifiedName: Name.Qualified = {
    val parentName = parent.getUnqualifiedName
    val portName = portInstance.getUnqualifiedName
    val identList = List(parentName, portName)
    Name.Qualified.fromIdentList(identList)
  }
}


object PortInstanceIdentifier {

  /** Creates a port instance identifier from an AST node */
  def fromNode(a: Analysis, node: AstNode[Ast.PortInstanceIdentifier]):
    Result.Result[PortInstanceIdentifier] = {
      val data = node.data
      (a.getComponentInstance(data.parent.id), a.getTopology(data.parent.id)) match {
        
      }

      for {
        componentInstance <- a.getComponentInstance(
          data.parent.id
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
