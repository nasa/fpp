package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP interface instance */
sealed trait InterfaceInstance {

  override def toString = getQualifiedName.toString

  /** Gets the qualified name of the interface instance */
  def getQualifiedName: Name.Qualified

  /** Gets the unqualified name of the interface instance */
  def getUnqualifiedName: String

  /** Gets the location of the interface instance */
  def getLoc: Location

  /** Get the full port interface of this instance */
  def getInterface: PortInterface

  /* Get a port instance given the name of the port instance */
  def getPortInstance(name: AstNode[Ast.Ident]): Result.Result[PortInstance] =
    getInterface.getPortInstance(name, getUnqualifiedName)

}

object InterfaceInstance {

  final case class InterfaceComponentInstance(ci: ComponentInstance) extends InterfaceInstance {
    override def getQualifiedName: Name.Qualified = ci.getQualifiedName
    override def getUnqualifiedName: String = ci.getUnqualifiedName
    override def getLoc: Location = ci.getLoc
    override def getInterface: PortInterface = ci.getInterface
  }

  final case class InterfaceTopology(top: Topology) extends InterfaceInstance {
    override def getQualifiedName: Name.Qualified = top.getQualifiedName
    override def getUnqualifiedName: String = top.getUnqualifiedName
    override def getLoc: Location = top.getLoc
    override def getInterface: PortInterface = top.portInterface
  }

  def fromComponentInstance(ci: ComponentInstance) =
    InterfaceComponentInstance(ci)

  def fromTopology(top: Topology) =
    InterfaceTopology(top)

}