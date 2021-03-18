package fpp.compiler.analysis

import fpp.compiler.ast._
import fpp.compiler.util._

/** An FPP port instance identifier */
case class PortInstanceIdentifier(
  /** The component instance */
  componentInstance: ComponentInstance,
  /** The port name */
  portName: Name.Unqualified,
  /** The port instance */
  portInstance: PortInstance
)
