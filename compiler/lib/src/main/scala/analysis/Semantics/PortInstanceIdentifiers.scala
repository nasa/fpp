package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** Check port instance identifiers */
object PortInstanceIdentifiers {

  /** Gets the qualified name of a port instance identifier */
  def getQualifiedName(a: Analysis, pid: PortInstanceIdentifier) = {
    val qn = ComponentInstances.getQualifiedName(
      a,
      pid.componentInstance
    )
    val identList = qn.toIdentList
    Name.Qualified.fromIdentList(identList :+ pid.portName)
  }

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
        node.data.portName.data,
        portInstance
      )
    }

}
