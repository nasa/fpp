package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP port instance identifier */
case class PortInstanceIdentifier(
  /** The interface instance */
  interfaceInstance: InterfaceInstance,
  /** The port instance */
  portInstance: PortInstance
) {

  override def toString = getQualifiedName.toString

  /** Gets the qualified name */
  def getQualifiedName: Name.Qualified = {
    val instanceName = interfaceInstance.getQualifiedName
    val identList = instanceName.toIdentList
    Name.Qualified.fromIdentList(identList :+ portInstance.getUnqualifiedName)
  }

  /** Gets the unqualified name */
  def getUnqualifiedName: Name.Qualified = {
    val instanceName = interfaceInstance.getUnqualifiedName
    val portName = portInstance.getUnqualifiedName
    val identList = List(instanceName, portName)
    Name.Qualified.fromIdentList(identList)
  }

}

object PortInstanceIdentifier {

  /** Creates a port instance identifier from an AST node */
  def fromNode(a: Analysis, node: AstNode[Ast.PortInstanceIdentifier]):
    Result.Result[PortInstanceIdentifier] = {
      val data = node.data
      for {
        interfaceInstance <- a.getInterfaceInstance(
          data.interfaceInstance.id
        )
        portInstance <- interfaceInstance.getPortInstance(
          data.portName
        )
      }
      yield PortInstanceIdentifier(
        interfaceInstance,
        portInstance
      )
    }

}
