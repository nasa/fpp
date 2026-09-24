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

  /** Get the underlying component instance from this interface instance, None if it's a topology */
  def getComponentInstanceOpt: Option[ComponentInstance]

  /* Get a port instance given the name of the port instance */
  def getPortInstance(name: AstNode[Ast.Ident]): Result.Result[PortInstance] =
    getInterface.getPortInstance(name, getUnqualifiedName)

}

object InterfaceInstance {

  /** Ordering on interface instances. */
  implicit val ordering: Ordering[InterfaceInstance] =
    new Ordering[InterfaceInstance] {

      /** An ordinal for each kind of instance, used to break ties between
       *  instances of different kinds that have the same qualified name */
      private def kind(ii: InterfaceInstance): Int = ii match {
        case _: InterfaceComponentInstance => 0
        case _: InterfaceTopology => 1
        case _: InterfaceTemplateArg => 2
      }

      override def compare(ii1: InterfaceInstance, ii2: InterfaceInstance): Int = {
        val nameCompare =
          ii1.getQualifiedName.toString.compare(ii2.getQualifiedName.toString)
        if (nameCompare != 0) nameCompare
        else {
          val kindCompare = kind(ii1).compare(kind(ii2))
          if (kindCompare != 0) kindCompare
          else (ii1, ii2) match {
            case (a1: InterfaceTemplateArg, a2: InterfaceTemplateArg) =>
              // Two bound template parameters with the same name are the same
              // instance only if they name the same parameter of the same
              // template and are bound to the same argument
              val argCompare = compare(a1.ii, a2.ii)
              if (argCompare != 0) argCompare
              else a1.paramDef.interface.id.compare(a2.paramDef.interface.id)
            // Component instance and topology names are unique in a model,
            // so equal names mean equal instances
            case _ => 0
          }
        }
      }

    }

  final case class InterfaceComponentInstance(ci: ComponentInstance) extends InterfaceInstance {
    override def getQualifiedName: Name.Qualified = ci.getQualifiedName
    override def getUnqualifiedName: String = ci.getUnqualifiedName
    override def getLoc: Location = ci.getLoc
    override def getInterface: PortInterface = ci.getInterface
    override def getComponentInstanceOpt: Option[ComponentInstance] = Some(ci)
  }

  final case class InterfaceTopology(top: Topology) extends InterfaceInstance {
    override def getQualifiedName: Name.Qualified = top.getQualifiedName
    override def getUnqualifiedName: String = top.getUnqualifiedName
    override def getLoc: Location = top.getLoc
    override def getInterface: PortInterface = top.portInterface
    override def getComponentInstanceOpt: Option[ComponentInstance] = None
  }

  final case class InterfaceTemplateArg(
    paramDef: Ast.TemplateParam.Interface,
    interface: Interface,
    ii: InterfaceInstance,
  ) extends InterfaceInstance {
    override def getQualifiedName: Name.Qualified = Name.Qualified.fromIdent(paramDef.name)
    override def getUnqualifiedName: String = paramDef.name
    override def getLoc: Location = Locations.get(paramDef.interface.id)
    override def getInterface: PortInterface = interface.portInterface
    override def getComponentInstanceOpt: Option[ComponentInstance] =
      ii.getComponentInstanceOpt

    override def getPortInstance(name: AstNode[Ast.Ident]): Result.Result[PortInstance] =
      getInterface.getPortInstance(name, interface.getUnqualifiedName)
  }

  def fromComponentInstance(ci: ComponentInstance) =
    InterfaceComponentInstance(ci)

  def fromTopology(top: Topology) =
    InterfaceTopology(top)

  def fromTemplateArg(
    paramDef: Ast.TemplateParam.Interface,
    interface: Interface,
    ii: InterfaceInstance
  ) = InterfaceTemplateArg(paramDef, interface, ii)

}
