package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP telemetry channel instance identifier */
case class TlmChannelIdentifier(
  /** The AST node */
  node: AstNode[Ast.TlmChannelIdentifier],
  /** The component instance */
  componentInstance: ComponentInstance,
  /** The telemetry channel */
  tlmChannel: TlmChannel
) {

  override def toString = getQualifiedName.toString

  /** Gets the location of telemetry channel identifier */
  def getLoc: Location = Locations.get(node.id)

  /** Gets the qualified name */
  def getQualifiedName: Name.Qualified = {
    val componentName = componentInstance.qualifiedName
    val identList = componentName.toIdentList
    Name.Qualified.fromIdentList(identList :+ tlmChannel.getName)
  }

  /** Gets the unqualified name */
  def getUnqualifiedName: Name.Qualified = {
    val componentName = componentInstance.getUnqualifiedName
    val channelName = tlmChannel.getName
    val identList = List(componentName, channelName)
    Name.Qualified.fromIdentList(identList)
  }

}

object TlmChannelIdentifier {

  /** Creates a telemetry channel identifier from an AST node */
  def fromNode(a: Analysis, node: AstNode[Ast.TlmChannelIdentifier]):
    Result.Result[TlmChannelIdentifier] = {
      val data = node.data
      for {
        componentInstance <- a.getComponentInstance(
          data.componentInstance.id
        )
        tlmChannel <- componentInstance.component.getTlmChannelByName(
          data.channelName
        )
      }
      yield TlmChannelIdentifier(
        node,
        componentInstance,
        tlmChannel
      )
    }

  /** Gets a numeric channel identifier from an AST node */
  def getNumericIdForNode
    (a: Analysis, d: Dictionary, t: Topology)
    (node: AstNode[Ast.TlmChannelIdentifier]):
  Result.Result[BigInt] =
    for {
      channelId <- TlmChannelIdentifier.fromNode(a, node)
      numericId <- d.findNumericIdForChannel (t) (channelId)
    }
    yield numericId

}
